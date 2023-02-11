package org.sarav.food.restaurant.service.messaging.mapper;


import org.sarav.food.order.RestaurantApprovalRequestAvroModel;
import org.sarav.food.order.RestaurantApprovalResponseAvroModel;
import org.sarav.food.order.RestaurantApprovalStatus;
import org.sarav.food.order.system.domain.valueobjects.ProductId;
import org.sarav.food.order.system.domain.valueobjects.RestaurantOrderStatus;
import org.sarav.food.restaurant.service.app.dto.RestaurantApprovalRequest;
import org.sarav.food.restaurant.service.app.outbox.model.OrderEventPayload;
import org.sarav.food.restaurant.service.domain.entity.Product;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class RestaurantMessagingDataMapper {


    public RestaurantApprovalResponseAvroModel
    orderEventPayloadToRestaurantApprovalResponseAvroModel(String sagaId, OrderEventPayload orderEventPayload) {
        return RestaurantApprovalResponseAvroModel.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setSagaId(sagaId)
                .setOrderId(orderEventPayload.getOrderId())
                .setRestaurantId(orderEventPayload.getRestaurantId())
                .setCreatedAt(orderEventPayload.getCreatedAt().toInstant())
                .setRestaurantApprovalStatus(RestaurantApprovalStatus.valueOf(orderEventPayload.getOrderApprovalStatus()))
                .setFailureMessages(orderEventPayload.getFailureMessages())
                .build();
    }

    public RestaurantApprovalRequest
    restaurantApprovalRequestAvroModelToRestaurantApproval(RestaurantApprovalRequestAvroModel
                                                                   restaurantApprovalRequestAvroModel) {
        return RestaurantApprovalRequest.builder()
                .id(restaurantApprovalRequestAvroModel.getId())
                .sagaId(restaurantApprovalRequestAvroModel.getSagaId())
                .restaurantId(restaurantApprovalRequestAvroModel.getRestaurantId())
                .orderId(restaurantApprovalRequestAvroModel.getOrderId())
                .restaurantOrderStatus(RestaurantOrderStatus.valueOf(restaurantApprovalRequestAvroModel
                        .getRestaurantApprovalStatus().name()))
                .products(restaurantApprovalRequestAvroModel.getProducts()
                        .stream().map(avroModel ->
                                Product.builder()
                                        .productId(new ProductId(UUID.fromString(avroModel.getId())))
                                        .quantity(avroModel.getQuantity())
                                        .build())
                        .collect(Collectors.toList()))
                .price(restaurantApprovalRequestAvroModel.getPrice())
                .createdAt(restaurantApprovalRequestAvroModel.getCreatedAt())
                .build();
    }
}
