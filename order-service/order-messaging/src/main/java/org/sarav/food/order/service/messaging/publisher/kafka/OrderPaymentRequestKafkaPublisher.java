package org.sarav.food.order.service.messaging.publisher.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.sarav.food.order.PaymentRequestAvroModel;
import org.sarav.food.order.service.app.config.OrderServiceConfigData;
import org.sarav.food.order.service.app.outbox.model.payment.OrderPaymentEventPayload;
import org.sarav.food.order.service.app.outbox.model.payment.OrderPaymentOutboxMessage;
import org.sarav.food.order.service.app.ports.output.message.publisher.payment.PaymentRequestMessagePublisher;
import org.sarav.food.order.service.messaging.mapper.OrderMessagingDataMapper;
import org.sarav.food.order.system.kafka.KafkaMessageHelper;
import org.sarav.food.order.system.kafka.service.KafkaProducer;
import org.sarav.food.order.system.outbox.OutboxStatus;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

@Slf4j
@Component
public class OrderPaymentRequestKafkaPublisher implements PaymentRequestMessagePublisher {

    private final KafkaProducer<String, PaymentRequestAvroModel> kafkaProducer;

    private final KafkaMessageHelper kafkaMessageHelper;

    private final OrderServiceConfigData orderServiceConfigData;
    private final OrderMessagingDataMapper orderMessagingDataMapper;
    private final ObjectMapper objectMapper;

    public OrderPaymentRequestKafkaPublisher(KafkaProducer<String, PaymentRequestAvroModel> kafkaProducer
            , KafkaMessageHelper kafkaMessageHelper, OrderServiceConfigData orderServiceConfigData, OrderMessagingDataMapper orderMessagingDataMapper, ObjectMapper objectMapper) {
        this.kafkaProducer = kafkaProducer;
        this.kafkaMessageHelper = kafkaMessageHelper;
        this.orderServiceConfigData = orderServiceConfigData;
        this.orderMessagingDataMapper = orderMessagingDataMapper;
        this.objectMapper = objectMapper;
    }


    @Override
    public void publish(OrderPaymentOutboxMessage orderPaymentOutboxMessage,
                        BiConsumer<OrderPaymentOutboxMessage, OutboxStatus> outboxUpdateCallback) {
        OrderPaymentEventPayload orderPaymentEventPayload =
                kafkaMessageHelper.getOrderEventPayload(orderPaymentOutboxMessage.getPayload(),
                        OrderPaymentEventPayload.class);
        String sagaId = orderPaymentOutboxMessage.getSagaId().toString();
        String paymentRequestTopicName = orderServiceConfigData.getPaymentRequestTopicName();
        log.info("Publishing the OrderPaymentEvent with SagaId {} for Order {}", sagaId, orderPaymentEventPayload.getOrderId());

        try {
            PaymentRequestAvroModel paymentRequestAvroModel =
                    orderMessagingDataMapper.orderPaymentEventPayloadToPaymentRequestAvroModel(
                            orderPaymentEventPayload, sagaId);
            kafkaProducer.sendMessage(paymentRequestTopicName, sagaId, // since sagaId is used as the key here, messages belonging to the same saga will end up in same partition
                    paymentRequestAvroModel,
                    kafkaMessageHelper.getKafkaCallback(paymentRequestTopicName,
                            paymentRequestAvroModel,
                            orderPaymentOutboxMessage,
                            outboxUpdateCallback,
                            sagaId, "PaymentRequestAvroModel"));
            log.info("OrderPaymentEvent has been successfully sent to the topic {}", paymentRequestTopicName);
        } catch (Exception e) {
            log.error("Error Occurred while sending OrderPaymentEvent to the topic {} with SagaId {} with message {}",
                    paymentRequestTopicName, sagaId, e.getMessage());
        }
    }
}
