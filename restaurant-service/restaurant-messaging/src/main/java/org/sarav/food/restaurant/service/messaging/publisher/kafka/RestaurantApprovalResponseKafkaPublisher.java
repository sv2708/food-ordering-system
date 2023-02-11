package org.sarav.food.restaurant.service.messaging.publisher.kafka;

import lombok.extern.slf4j.Slf4j;
import org.sarav.food.order.RestaurantApprovalResponseAvroModel;
import org.sarav.food.order.system.kafka.KafkaMessageHelper;
import org.sarav.food.order.system.kafka.service.KafkaProducer;
import org.sarav.food.order.system.outbox.OutboxStatus;
import org.sarav.food.restaurant.service.app.config.RestaurantServiceConfigData;
import org.sarav.food.restaurant.service.app.outbox.model.OrderEventPayload;
import org.sarav.food.restaurant.service.app.outbox.model.OrderOutboxMessage;
import org.sarav.food.restaurant.service.app.ports.output.message.publisher.RestaurantApprovalResponseMessagePublisher;
import org.sarav.food.restaurant.service.messaging.mapper.RestaurantMessagingDataMapper;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

@Slf4j
@Component
public class RestaurantApprovalResponseKafkaPublisher implements RestaurantApprovalResponseMessagePublisher {

    private final RestaurantMessagingDataMapper restaurantMessagingDataMapper;
    private final KafkaProducer<String, RestaurantApprovalResponseAvroModel> kafkaProducer;
    private final RestaurantServiceConfigData restaurantServiceConfigData;
    private final KafkaMessageHelper kafkaMessageHelper;

    public RestaurantApprovalResponseKafkaPublisher(RestaurantMessagingDataMapper restaurantMessagingDataMapper,
                                                    KafkaProducer<String, RestaurantApprovalResponseAvroModel> kafkaProducer,
                                                    RestaurantServiceConfigData restaurantServiceConfigData,
                                                    KafkaMessageHelper kafkaMessageHelper) {
        this.restaurantMessagingDataMapper = restaurantMessagingDataMapper;
        this.kafkaProducer = kafkaProducer;
        this.restaurantServiceConfigData = restaurantServiceConfigData;
        this.kafkaMessageHelper = kafkaMessageHelper;
    }

    @Override
    public void publish(OrderOutboxMessage orderOutboxMessage,
                        BiConsumer<OrderOutboxMessage, OutboxStatus> outboxCallback) {
        OrderEventPayload orderEventPayload =
                kafkaMessageHelper.getOrderEventPayload(orderOutboxMessage.getPayload(),
                        OrderEventPayload.class);

        String sagaId = orderOutboxMessage.getSagaId().toString();

        log.info("Received OrderOutboxMessage for order id: {} and saga id: {}",
                orderEventPayload.getOrderId(),
                sagaId);
        try {
            RestaurantApprovalResponseAvroModel restaurantApprovalResponseAvroModel =
                    restaurantMessagingDataMapper
                            .orderEventPayloadToRestaurantApprovalResponseAvroModel(sagaId, orderEventPayload);

            kafkaProducer.sendMessage(restaurantServiceConfigData.getRestaurantApprovalResponseTopicName(),
                    sagaId,
                    restaurantApprovalResponseAvroModel,
                    kafkaMessageHelper.getKafkaCallback(restaurantServiceConfigData
                                    .getRestaurantApprovalResponseTopicName(),
                            restaurantApprovalResponseAvroModel,
                            orderOutboxMessage,
                            outboxCallback,
                            orderEventPayload.getOrderId(),
                            "RestaurantApprovalResponseAvroModel"));

            log.info("RestaurantApprovalResponseAvroModel sent to kafka for order id: {} and saga id: {}",
                    restaurantApprovalResponseAvroModel.getOrderId(), sagaId);
        } catch (Exception e) {
            log.error("Error while sending RestaurantApprovalResponseAvroModel message" +
                            " to kafka with order id: {} and saga id: {}, error: {}",
                    orderEventPayload.getOrderId(), sagaId, e.getMessage());
        }
    }
}
