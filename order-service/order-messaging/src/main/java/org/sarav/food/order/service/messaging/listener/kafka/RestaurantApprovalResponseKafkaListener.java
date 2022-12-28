package org.sarav.food.order.service.messaging.listener.kafka;

import lombok.extern.slf4j.Slf4j;
import org.sarav.food.order.RestaurantApprovalResponseAvroModel;
import org.sarav.food.order.RestaurantApprovalStatus;
import org.sarav.food.order.service.app.ports.input.message.listener.restaurantapproval.RestaurantApprovalResponseMessageListener;
import org.sarav.food.order.service.messaging.mapper.OrderMessagingDataMapper;
import org.sarav.food.order.system.kafka.service.KafkaConsumer;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.sarav.food.order.service.domain.entity.Order.FAILURE_MSG_DELIMITER;

@Slf4j
@Component
public class RestaurantApprovalResponseKafkaListener implements KafkaConsumer<RestaurantApprovalResponseAvroModel> {

    private final RestaurantApprovalResponseMessageListener restaurantApprovalResponseMessageListener;
    private final OrderMessagingDataMapper orderMessagingDataMapper;

    public RestaurantApprovalResponseKafkaListener(RestaurantApprovalResponseMessageListener restaurantApprovalResponseMessageListener, OrderMessagingDataMapper orderMessagingDataMapper) {
        this.restaurantApprovalResponseMessageListener = restaurantApprovalResponseMessageListener;
        this.orderMessagingDataMapper = orderMessagingDataMapper;
    }

    @Override
    @KafkaListener(id = "${kafka-consumer-config.restaurant-approval-consumer-group-id}",
            topics = "${order-service.restaurant-approval-response-topic-name}")
    public void receive(@Payload List<RestaurantApprovalResponseAvroModel> messages,
                        @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) List<String> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION_ID) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {
        log.info("Received {} messages with headers of key: {}, Partitions: {}, Offsets: {} ",
                messages.size(), keys.toString(), partitions.toString(), offsets.toString());

        messages.forEach(avroModel -> {
            if (avroModel.getRestaurantApprovalStatus() == RestaurantApprovalStatus.APPROVED) {
                log.info("Received Message Restaurant Successfully approved response for {} in Restaurant: {}",
                        avroModel.getOrderId(),
                        avroModel.getRestaurantId());
                restaurantApprovalResponseMessageListener.orderApproved(
                        orderMessagingDataMapper.restaurantApprovalResponseAvroModelToRestaurantApprovalResponse(avroModel));
            } else if (avroModel.getRestaurantApprovalStatus() == RestaurantApprovalStatus.REJECTED) {
                log.info("Received Message Restaurant Approval Failed Response for {} in Restaurant: {}",
                        avroModel.getOrderId(),
                        avroModel.getRestaurantId());
                log.warn(String.join(FAILURE_MSG_DELIMITER, avroModel.getFailureMessages()));
                restaurantApprovalResponseMessageListener.orderRejected(
                        orderMessagingDataMapper.restaurantApprovalResponseAvroModelToRestaurantApprovalResponse(avroModel)
                );
            } else {
                log.error("Invalid Restaurant Approval Response Received");
                log.warn(String.join(FAILURE_MSG_DELIMITER, avroModel.getFailureMessages()));
            }
        });

    }


}
