package org.sarav.food.order.service.messaging.mapper;

import org.sarav.food.order.*;
import org.sarav.food.order.service.app.dto.message.PaymentResponse;
import org.sarav.food.order.service.app.dto.message.RestaurantApprovalResponse;
import org.sarav.food.order.service.domain.event.OrderCancelledEvent;
import org.sarav.food.order.service.domain.event.OrderCreatedEvent;
import org.sarav.food.order.service.domain.event.OrderPaidEvent;
import org.sarav.food.order.system.domain.valueobjects.OrderApprovalStatus;
import org.sarav.food.order.system.domain.valueobjects.PaymentStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.stream.Collectors;

@Component
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

    public PaymentResponse paymentResponseAvroModelToPaymentResponse(PaymentResponseAvroModel paymentResponseAvroModel) {
        return PaymentResponse.builder().id(paymentResponseAvroModel.getId())
                .sagaId(paymentResponseAvroModel.getSagaId())
                .orderId(paymentResponseAvroModel.getOrderId())
                .customerId(paymentResponseAvroModel.getCustomerId())
                .createdAt(paymentResponseAvroModel.getCreatedAt())
                .paymentStatus(PaymentStatus.valueOf(paymentResponseAvroModel.getOrderPaymentStatus().name()))
                .price(paymentResponseAvroModel.getPrice())
                .failureMessages(paymentResponseAvroModel.getFailureMessages())
                .build();
    }

    public RestaurantApprovalResponse restaurantApprovalResponseAvroModelToRestaurantApprovalResponse(RestaurantApprovalResponseAvroModel avroModel) {
        return RestaurantApprovalResponse.builder().id(avroModel.getId())
                .orderId(avroModel.getOrderId())
                .restaurantId(avroModel.getRestaurantId())
                .sagaId(avroModel.getSagaId())
                .failureMessages(avroModel.getFailureMessages())
                .orderApprovalStatus(OrderApprovalStatus.valueOf(avroModel.getRestaurantApprovalStatus().name()))
                .createdAt(avroModel.getCreatedAt())
                .build();
    }
}
