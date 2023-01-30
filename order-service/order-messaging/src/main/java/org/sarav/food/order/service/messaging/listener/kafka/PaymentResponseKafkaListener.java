package org.sarav.food.order.service.messaging.listener.kafka;

import lombok.extern.slf4j.Slf4j;
import org.sarav.food.order.OrderPaymentStatus;
import org.sarav.food.order.PaymentResponseAvroModel;
import org.sarav.food.order.service.app.ports.input.message.listener.payment.PaymentResponseMessageListener;
import org.sarav.food.order.service.domain.exception.OrderNotFoundException;
import org.sarav.food.order.service.messaging.mapper.OrderMessagingDataMapper;
import org.sarav.food.order.system.kafka.service.KafkaConsumer;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class PaymentResponseKafkaListener implements KafkaConsumer<PaymentResponseAvroModel> {

    private final PaymentResponseMessageListener paymentResponseMessageListener;
    private final OrderMessagingDataMapper orderMessagingDataMapper;

    public PaymentResponseKafkaListener(PaymentResponseMessageListener paymentResponseMessageListener, OrderMessagingDataMapper orderMessagingDataMapper) {
        this.paymentResponseMessageListener = paymentResponseMessageListener;
        this.orderMessagingDataMapper = orderMessagingDataMapper;
    }

    @Override
    @KafkaListener(id = "${kafka-consumer-config.payment-consumer-group-id}",
            topics = "${order-service.payment-response-topic-name}")
    public void receive(@Payload List<PaymentResponseAvroModel> messages,
                        @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) List<String> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION_ID) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {

        log.info("Received {} messages with headers of key: {}, Partitions: {}, Offsets: {} ",
                messages.size(), keys.toString(), partitions.toString(), offsets.toString());

        messages.forEach(paymentResponseAvroModel -> {
            try {
                if (paymentResponseAvroModel.getOrderPaymentStatus() == OrderPaymentStatus.COMPLETED) {
                    log.info("Processing Successful payment of OrderId {}", paymentResponseAvroModel.getOrderId());
                    paymentResponseMessageListener
                            .paymentCompleted(orderMessagingDataMapper.paymentResponseAvroModelToPaymentResponse(paymentResponseAvroModel));
                } else {
                    log.info("Failed Processing the payment of OrderId {}", paymentResponseAvroModel.getOrderId());
                    paymentResponseMessageListener
                            .paymentCancelled(orderMessagingDataMapper.paymentResponseAvroModelToPaymentResponse(paymentResponseAvroModel));
                } /**
                 handling the below exceptions will prevent kafka listener from retrying the failed message
                 as these errors cannot be resolved when retrying.
                 */
            } catch (OptimisticLockingFailureException e) {
                // This Exception will occur when same outbox message is processed more than by the system.
                log.error("Optimistic Locking Exception Occurred as the message for Order Payment {} was already processed. Error: {}",
                        paymentResponseAvroModel.getOrderId(), e.getMessage());
            } catch (OrderNotFoundException e) {
                log.error("Order with Id {} is not found.", paymentResponseAvroModel.getOrderId());
            }
        });

    }
}
