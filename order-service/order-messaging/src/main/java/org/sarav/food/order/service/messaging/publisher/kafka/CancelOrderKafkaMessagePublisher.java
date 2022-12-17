package org.sarav.food.order.service.messaging.publisher.kafka;

import lombok.extern.slf4j.Slf4j;
import org.sarav.food.order.PaymentRequestAvroModel;
import org.sarav.food.order.service.app.config.OrderServiceConfigData;
import org.sarav.food.order.service.app.ports.output.message.publisher.payment.OrderCancelledPaymentRequestMessagePublisher;
import org.sarav.food.order.service.domain.event.OrderCancelledEvent;
import org.sarav.food.order.service.messaging.mapper.OrderMessagingDataMapper;
import org.sarav.food.order.system.kafka.KafkaMesssageHelper;
import org.sarav.food.order.system.kafka.service.KafkaProducer;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CancelOrderKafkaMessagePublisher implements OrderCancelledPaymentRequestMessagePublisher {

    private final OrderServiceConfigData orderServiceConfigData;
    private final KafkaProducer<String, PaymentRequestAvroModel> kafkaProducer;
    private final OrderMessagingDataMapper orderMessagingDataMapper;
    private final KafkaMesssageHelper kafkaMesssageHelper;

    public CancelOrderKafkaMessagePublisher(OrderServiceConfigData orderServiceConfigData, KafkaProducer<String, PaymentRequestAvroModel> kafkaProducer, OrderMessagingDataMapper orderMessagingDataMapper, KafkaMesssageHelper kafkaMesssageHelper) {
        this.orderServiceConfigData = orderServiceConfigData;
        this.kafkaProducer = kafkaProducer;
        this.orderMessagingDataMapper = orderMessagingDataMapper;
        this.kafkaMesssageHelper = kafkaMesssageHelper;
    }

    /**
     * Kafka message info
     * Key - Order Id
     * value - Payment Request Avro Model
     */
    @Override
    public void publish(OrderCancelledEvent domainEvent) {
        var orderId = domainEvent.getOrder().getId().getValue().toString();
        try {
            var paymentRequestAvroModel = orderMessagingDataMapper
                    .orderCancelledEventToPaymentRequestAvroModel(domainEvent);
            var topicName = orderServiceConfigData.getPaymentRequestTopicName();
            kafkaProducer.sendMessage(topicName,
                    orderId,
                    paymentRequestAvroModel,
                    kafkaMesssageHelper.getKafkaCallback(topicName, paymentRequestAvroModel, orderId, "PaymentRequestAvroModel")
            );
            log.info("Order Cancelled Event message successfully sent for Order Id {} on topic {}",
                    paymentRequestAvroModel.getOrderId(), topicName);
        } catch (Exception e) {
            log.error("Error Occurred while sending OrderCancelledEvent message for {} to Kafka {}",
                    orderId,
                    e.getMessage());
        }

    }

}
