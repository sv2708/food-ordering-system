package org.sarav.food.order.service.messaging.publisher.kafka;

import lombok.extern.slf4j.Slf4j;
import org.sarav.food.order.PaymentRequestAvroModel;
import org.sarav.food.order.service.app.config.OrderServiceConfigData;
import org.sarav.food.order.service.app.ports.output.message.publisher.payment.OrderCreatedPaymentRequestMessagePublisher;
import org.sarav.food.order.service.domain.event.OrderCreatedEvent;
import org.sarav.food.order.service.messaging.mapper.OrderMessagingDataMapper;
import org.sarav.food.order.system.kafka.service.KafkaProducer;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CreateOrderKafkaMessagePublisher implements OrderCreatedPaymentRequestMessagePublisher {

    private final OrderServiceConfigData orderServiceConfigData;
    private final KafkaProducer<String, PaymentRequestAvroModel> kafkaProducer;
    private final OrderMessagingDataMapper orderMessagingDataMapper;
    private final OrderKafkaMesssageHelper orderKafkaMesssageHelper;

    public CreateOrderKafkaMessagePublisher(OrderServiceConfigData orderServiceConfigData, KafkaProducer<String, PaymentRequestAvroModel> kafkaProducer, OrderMessagingDataMapper orderMessagingDataMapper, OrderKafkaMesssageHelper orderKafkaMesssageHelper) {
        this.orderServiceConfigData = orderServiceConfigData;
        this.kafkaProducer = kafkaProducer;
        this.orderMessagingDataMapper = orderMessagingDataMapper;
        this.orderKafkaMesssageHelper = orderKafkaMesssageHelper;
    }

    /**
     * Kafka message info
     * Key - Order Id
     * value - Payment Request Avro Model
     */
    @Override
    public void publish(OrderCreatedEvent domainEvent) {
        var orderId = domainEvent.getOrder().getId().getValue().toString();
        try {
            var paymentRequestAvroModel = orderMessagingDataMapper
                    .orderCreatedEventToPaymentRequestAvroModel(domainEvent);
            var topicName = orderServiceConfigData.getPaymentRequestTopicName();
            kafkaProducer.sendMessage(topicName,
                    orderId,
                    paymentRequestAvroModel,
                    orderKafkaMesssageHelper.getKafkaCallback(topicName, paymentRequestAvroModel, orderId, "PaymentRequestAvroModel")
            );
            log.info("Order Created Event message successfully sent for Order Id {} on topic {}",
                    orderId, topicName);
        } catch (Exception e) {
            log.error("Error Occurred while sending OrderCreatedEvent message for {} to Kafka {}",
                    orderId,
                    e.getMessage());
        }

    }

}