package org.sarav.food.payment.service.messaging.publisher.kafka;

import lombok.extern.slf4j.Slf4j;
import org.sarav.food.order.PaymentResponseAvroModel;
import org.sarav.food.order.system.kafka.KafkaMessageHelper;
import org.sarav.food.order.system.kafka.service.KafkaProducer;
import org.sarav.food.order.system.outbox.OutboxStatus;
import org.sarav.food.payment.service.app.config.PaymentServiceConfigData;
import org.sarav.food.payment.service.app.outbox.model.OrderEventPayload;
import org.sarav.food.payment.service.app.outbox.model.OrderOutboxMessage;
import org.sarav.food.payment.service.app.ports.output.message.publisher.PaymentResponseMessagePublisher;
import org.sarav.food.payment.service.messaging.mapper.PaymentMessagingDataMapper;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

@Slf4j
@Component
public class OrderPaymentResponseKafkaPublisher implements PaymentResponseMessagePublisher {

    private final KafkaProducer<String, PaymentResponseAvroModel> kafkaProducer;
    private final KafkaMessageHelper kafkaMessageHelper;
    private final PaymentServiceConfigData paymentServiceConfigData;
    private final PaymentMessagingDataMapper paymentMessagingDataMapper;

    public OrderPaymentResponseKafkaPublisher(KafkaProducer<String, PaymentResponseAvroModel> kafkaProducer, KafkaMessageHelper kafkaMessageHelper, PaymentServiceConfigData paymentServiceConfigData, PaymentMessagingDataMapper paymentMessagingDataMapper) {
        this.kafkaProducer = kafkaProducer;
        this.kafkaMessageHelper = kafkaMessageHelper;
        this.paymentServiceConfigData = paymentServiceConfigData;
        this.paymentMessagingDataMapper = paymentMessagingDataMapper;
    }

    @Override
    public void publish(OrderOutboxMessage orderOutboxMessage, BiConsumer<OrderOutboxMessage, OutboxStatus> outboxCallback) {

        log.info("Publishing OrderOutboxMessage {} to Kafka for Saga {}", orderOutboxMessage.getId(), orderOutboxMessage.getSagaId());
        String sagaId = orderOutboxMessage.getSagaId().toString();
        OrderEventPayload orderEventPayload = kafkaMessageHelper.getOrderEventPayload(orderOutboxMessage.getPayload(),
                OrderEventPayload.class);
        PaymentResponseAvroModel paymentResponseAvroModel = paymentMessagingDataMapper
                .orderEventPayloadToPaymentResponseAvroModel(sagaId, orderEventPayload);
        // kafka message Key -> sagaId, Value -> PaymentResponseAvroModel
        try {
            kafkaProducer.sendMessage(
                    paymentServiceConfigData.getPaymentResponseTopicName(),
                    sagaId,
                    paymentResponseAvroModel,
                    kafkaMessageHelper.getKafkaCallback(
                            paymentServiceConfigData.getPaymentResponseTopicName(),
                            paymentResponseAvroModel,
                            orderOutboxMessage,
                            outboxCallback,
                            orderEventPayload.getOrderId(),
                            "PaymentResponseAvroModel")
            );

            log.info("Published the message to the topic {} for Saga {}",
                    paymentServiceConfigData.getPaymentResponseTopicName(),
                    sagaId);
        } catch (Exception e) {
            log.error("Error Publishing PaymentResponse message to Kafka for Order {} with Saga {}. Error: {}",
                    orderEventPayload.getOrderId(), sagaId, e.getMessage());
        }

    }
}
