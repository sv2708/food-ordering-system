package org.sarav.food.order.service.messaging.publisher.kafka;

import lombok.extern.slf4j.Slf4j;
import org.sarav.food.order.RestaurantApprovalRequestAvroModel;
import org.sarav.food.order.service.app.config.OrderServiceConfigData;
import org.sarav.food.order.service.app.ports.output.message.publisher.restaurantapproval.OrderPaidRestaurantRequestMessagePublisher;
import org.sarav.food.order.service.domain.event.OrderPaidEvent;
import org.sarav.food.order.service.messaging.mapper.OrderMessagingDataMapper;
import org.sarav.food.order.system.kafka.KafkaMesssageHelper;
import org.sarav.food.order.system.kafka.service.KafkaProducer;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PayOrderKafkaMessagePublisher implements OrderPaidRestaurantRequestMessagePublisher {

    private final OrderServiceConfigData orderServiceConfigData;
    private final KafkaProducer<String, RestaurantApprovalRequestAvroModel> kafkaProducer;
    private final OrderMessagingDataMapper orderMessagingDataMapper;
    private final KafkaMesssageHelper kafkaMesssageHelper;

    public PayOrderKafkaMessagePublisher(OrderServiceConfigData orderServiceConfigData,
                                         KafkaProducer<String, RestaurantApprovalRequestAvroModel> kafkaProducer,
                                         OrderMessagingDataMapper orderMessagingDataMapper,
                                         KafkaMesssageHelper kafkaMesssageHelper) {
        this.orderServiceConfigData = orderServiceConfigData;
        this.kafkaProducer = kafkaProducer;
        this.orderMessagingDataMapper = orderMessagingDataMapper;
        this.kafkaMesssageHelper = kafkaMesssageHelper;
    }

    @Override
    public void publish(OrderPaidEvent domainEvent) {
        var orderId = domainEvent.getOrder().getId().getValue().toString();
        try {
            var restaurantApprovalRequestAvroModel = orderMessagingDataMapper.orderPaidEventToRestaurantApprovalRequestAvroModel(domainEvent);
            var topicName = orderServiceConfigData.getRestaurantApprovalRequestTopicName();
            kafkaProducer.sendMessage(topicName,
                    orderId,
                    restaurantApprovalRequestAvroModel,
                    kafkaMesssageHelper.getKafkaCallback(topicName, restaurantApprovalRequestAvroModel, orderId, "RestaurantApprovalRequestAvroModel")
            );
            log.info("Successfully published message to kafka topic {} for order {} ", topicName, orderId);
        } catch (Exception e) {
            log.error("Error Occurred when trying to publish Restaurant Approval Request for Order {} ", orderId);
        }

    }

}
