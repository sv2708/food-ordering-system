package org.sarav.food.payment.service.messaging.listener.kafka;

import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PSQLState;
import org.sarav.food.order.OrderPaymentStatus;
import org.sarav.food.order.PaymentRequestAvroModel;
import org.sarav.food.order.system.kafka.service.KafkaConsumer;
import org.sarav.food.payment.service.app.dto.PaymentRequest;
import org.sarav.food.payment.service.app.exception.PaymentApplicationServiceException;
import org.sarav.food.payment.service.app.ports.input.message.listener.PaymentRequestMessageListener;
import org.sarav.food.payment.service.domain.exception.PaymentNotFoundException;
import org.sarav.food.payment.service.messaging.mapper.PaymentMessagingDataMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
public class PaymentRequestKafkaListener implements KafkaConsumer<PaymentRequestAvroModel> {

    private final PaymentRequestMessageListener paymentRequestMessageListener;
    private final PaymentMessagingDataMapper paymentMessagingDataMapper;

    public PaymentRequestKafkaListener(PaymentRequestMessageListener paymentRequestMessageListener,
                                       PaymentMessagingDataMapper paymentMessagingDataMapper) {
        this.paymentRequestMessageListener = paymentRequestMessageListener;
        this.paymentMessagingDataMapper = paymentMessagingDataMapper;
    }

    @Override
    @KafkaListener(id = "${kafka-consumer-config.payment-consumer-group-id}",
            topics = "${payment-service.payment-request-topic-name}")
    public void receive(@Payload List<PaymentRequestAvroModel> messages,
                        @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) List<String> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION_ID) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {
        log.info("{} Messages received for keys {} in partitions: {} with offsets: {}",
                messages.size(), keys, partitions, offsets);

        messages.forEach(paymentRequestAvroModel -> {
                    try {
                        PaymentRequest paymentRequest = paymentMessagingDataMapper
                                .paymentRequestAvroModelToPaymentRequest(paymentRequestAvroModel);
                        if (paymentRequestAvroModel.getOrderPaymentStatus() == OrderPaymentStatus.PENDING) {
                            paymentRequestMessageListener
                                    .completePayment(paymentRequest);
                        } else if (paymentRequestAvroModel.getOrderPaymentStatus() == OrderPaymentStatus.ORDER_CANCELLED) {
                            paymentRequestMessageListener.cancelPayment(paymentRequest);
                        }
                        /**
                         * In PaymentResponseMessageListener we are catching OptimisticLockingException
                         * That could occur during the update of the OutboxMessage after PaymentResponse.
                         * Also, DataAccessException could occur when updating the unique index of saga_id, saga_status.
                         * Here in PaymentRequestMessageListener the OutboxMessage is being created and not updated so OptimisticLockingException cannot occur here.
                         * So in case of multiple messages, multiple times same entry in outbox table will be created causing Unique Constraint Violation
                         * Here we catch only DataAccessException.
                         */
                    } catch (DataAccessException e) {
                        log.error("Error occurred when processing the consumed PaymentRequest message for Order {} for Saga {}",
                                paymentRequestAvroModel.getOrderId(), paymentRequestAvroModel.getSagaId());
                        SQLException sqlException = (SQLException) e.getRootCause();

                        /**
                         * If Unique Constraint violation occurred because of Unique SagaId and SagaStatus index,
                         * then Kafka Should not process the message again.
                         * Else re-throw PaymentApplicationServiceException to retry the message processing.
                         */
                        if (sqlException != null && sqlException.getSQLState() != null &&
                                sqlException.getSQLState().equals(PSQLState.UNIQUE_VIOLATION.getState())) {
                            log.error("Unique Constraint Violation Occurred with SQL State {} for Order {}",
                                    sqlException.getSQLState(), paymentRequestAvroModel.getOrderId());
                        } else {
                            throw new PaymentApplicationServiceException("DataAccessException Occured for Order " + paymentRequestAvroModel.getOrderId() + " with Saga " + paymentRequestAvroModel.getSagaId());
                        }
                    } catch (PaymentNotFoundException e) {
                        log.error("Payment is not found for Order {} with Saga {}",
                                paymentRequestAvroModel.getOrderId(), paymentRequestAvroModel.getSagaId());
                    }
                }

        );

    }
}
