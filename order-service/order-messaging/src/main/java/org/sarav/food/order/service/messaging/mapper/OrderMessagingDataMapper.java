package org.sarav.food.order.service.messaging.mapper;

import org.sarav.food.order.*;
import org.sarav.food.order.service.domain.event.OrderCancelledEvent;
import org.sarav.food.order.service.domain.event.OrderCreatedEvent;
import org.sarav.food.order.service.domain.event.OrderPaidEvent;

import java.util.UUID;
import java.util.stream.Collectors;

public class OrderMessagingDataMapper {

    public PaymentRequestAvroModel orderCreatedEventToPaymentRequestAvroModel(OrderCreatedEvent orderCreatedEvent) {

        return PaymentRequestAvroModel.newBuilder()
                .setCreatedAt(orderCreatedEvent.getCreatedAt().toInstant())
                .setOrderId(orderCreatedEvent.getOrder().getId().getValue().toString())
                .setId(UUID.randomUUID().toString())
                .setOrderPaymentStatus(OrderPaymentStatus.PENDING)
                .setSagaId("")
                .setPrice(orderCreatedEvent.getOrder().getPrice().getAmount())
                .build();
    }

    public PaymentRequestAvroModel orderCancelledEventToPaymentRequestAvroModel(OrderCancelledEvent orderCancelledEvent) {

        return PaymentRequestAvroModel.newBuilder()
                .setCreatedAt(orderCancelledEvent.getCreatedAt().toInstant())
                .setOrderId(orderCancelledEvent.getOrder().getId().getValue().toString())
                .setId(UUID.randomUUID().toString())
                .setOrderPaymentStatus(OrderPaymentStatus.ORDER_CANCELLED)
                .setSagaId("")
                .setPrice(orderCancelledEvent.getOrder().getPrice().getAmount())
                .build();
    }


    public RestaurantApprovalRequestAvroModel orderPaidEventToRestaurantApprovalRequestAvroModel(OrderPaidEvent domainEvent) {

        return RestaurantApprovalRequestAvroModel.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setSagaId("")
                .setOrderId(domainEvent.getOrder().getId().getValue().toString())
                .setCustomerId(domainEvent.getOrder().getCustomerId().getValue().toString())
                .setRestaurantId(domainEvent.getOrder().getRestaurantId().getValue().toString())
                .setCreatedAt(domainEvent.getCreatedAt().toInstant())
                .setProducts(domainEvent.getOrder().getItems().stream().map(orderItem -> Product.newBuilder()
                        .setId(orderItem.getId().getValue().toString())
                        .setQuantity(orderItem.getQuantity())
                        .build()
                ).collect(Collectors.toList()))
                .setPrice(domainEvent.getOrder().getPrice().getAmount())
                .setRestaurantApprovalStatus(RestaurantApprovalStatus.PAID)
                .build();
    }
}
