package org.sarav.food.order.service.messaging.publisher.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.sarav.food.order.RestaurantApprovalRequestAvroModel;
import org.sarav.food.order.service.app.config.OrderServiceConfigData;
import org.sarav.food.order.service.app.outbox.model.approval.OrderApprovalEventPayload;
import org.sarav.food.order.service.app.outbox.model.approval.OrderApprovalOutboxMessage;
import org.sarav.food.order.service.app.ports.output.message.publisher.restaurantapproval.RestaurantApprovalRequestMessagePublisher;
import org.sarav.food.order.service.messaging.mapper.OrderMessagingDataMapper;
import org.sarav.food.order.system.kafka.KafkaMessageHelper;
import org.sarav.food.order.system.kafka.service.KafkaProducer;
import org.sarav.food.order.system.outbox.OutboxStatus;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

@Slf4j
@Component
public class RestaurantApprovalRequestKafkaPublisher implements RestaurantApprovalRequestMessagePublisher {

    private final KafkaProducer<String, RestaurantApprovalRequestAvroModel> kafkaProducer;

    private final KafkaMessageHelper kafkaMessageHelper;

    private final OrderServiceConfigData orderServiceConfigData;
    private final OrderMessagingDataMapper orderMessagingDataMapper;
    private final ObjectMapper objectMapper;

    public RestaurantApprovalRequestKafkaPublisher(KafkaProducer<String, RestaurantApprovalRequestAvroModel> kafkaProducer, KafkaMessageHelper kafkaMessageHelper, OrderServiceConfigData orderServiceConfigData, OrderMessagingDataMapper orderMessagingDataMapper, ObjectMapper objectMapper) {
        this.kafkaProducer = kafkaProducer;
        this.kafkaMessageHelper = kafkaMessageHelper;
        this.orderServiceConfigData = orderServiceConfigData;
        this.orderMessagingDataMapper = orderMessagingDataMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publish(OrderApprovalOutboxMessage orderApprovalOutboxMessage,
                        BiConsumer<OrderApprovalOutboxMessage, OutboxStatus> approvalOutboxUpdateCallback) {

        OrderApprovalEventPayload orderApprovalEventPayload = kafkaMessageHelper.getOrderEventPayload(
                orderApprovalOutboxMessage.getPayload(), OrderApprovalEventPayload.class);
        String sagaId = orderApprovalOutboxMessage.getSagaId().toString();
        String orderApprovalTopicName = orderServiceConfigData.getRestaurantApprovalRequestTopicName();
        log.info("Publishing the OrderApprovalEvent with SagaId {} for Order {}", sagaId, orderApprovalEventPayload.getOrderId());

        try {
            RestaurantApprovalRequestAvroModel restaurantApprovalRequestAvroModel =
                    orderMessagingDataMapper.orderApprovalEventPayloadToRestaurantApprovalRequestAvroModel(orderApprovalEventPayload, sagaId);
            kafkaProducer.sendMessage(orderApprovalTopicName, sagaId, restaurantApprovalRequestAvroModel,
                    kafkaMessageHelper.getKafkaCallback(
                            orderApprovalTopicName, restaurantApprovalRequestAvroModel, orderApprovalOutboxMessage,
                            approvalOutboxUpdateCallback, sagaId, "RestaurantApprovalRequestAvroModel"
                    ));
        } catch (Exception e) {
            log.error("Error Publishing the OrderApprovalEvent with SagaId {} for Order {}. Error: {}",
                    sagaId, orderApprovalEventPayload.getOrderId(), e.getMessage());
        }

    }

}
