package org.sarav.food.order.service.messaging.mapper;

import org.sarav.food.order.*;
import org.sarav.food.order.service.app.dto.message.CustomerModel;
import org.sarav.food.order.service.app.dto.message.PaymentResponse;
import org.sarav.food.order.service.app.dto.message.RestaurantApprovalResponse;
import org.sarav.food.order.service.app.outbox.model.approval.OrderApprovalEventPayload;
import org.sarav.food.order.service.app.outbox.model.payment.OrderPaymentEventPayload;
import org.sarav.food.order.system.domain.valueobjects.OrderApprovalStatus;
import org.sarav.food.order.system.domain.valueobjects.PaymentStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class OrderMessagingDataMapper {

    public PaymentRequestAvroModel orderPaymentEventPayloadToPaymentRequestAvroModel(OrderPaymentEventPayload orderPaymentEventPayload, String sagaId) {
        return PaymentRequestAvroModel.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setOrderId(orderPaymentEventPayload.getOrderId())
                .setCustomerId(orderPaymentEventPayload.getCustomerId())
                .setOrderPaymentStatus(OrderPaymentStatus.valueOf(orderPaymentEventPayload.getPaymentOrderStatus()))
                .setPrice(orderPaymentEventPayload.getAmount())
                .setSagaId(sagaId)
                .setCreatedAt(orderPaymentEventPayload.getCreatedAt().toInstant())
                .build();
    }

    public RestaurantApprovalRequestAvroModel
    orderApprovalEventPayloadToRestaurantApprovalRequestAvroModel(OrderApprovalEventPayload orderApprovalEventPayload,
                                                                  String sagaId) {
        return RestaurantApprovalRequestAvroModel.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setSagaId(sagaId)
                .setOrderId(orderApprovalEventPayload.getOrderId())
                .setRestaurantId(orderApprovalEventPayload.getRestaurantId())
                .setPrice(orderApprovalEventPayload.getPrice())
                .setCreatedAt(orderApprovalEventPayload.getCreatedAt().toInstant())
                .setRestaurantApprovalStatus(RestaurantApprovalStatus.valueOf(orderApprovalEventPayload.getRestaurantApprovalStatus()))
                .setProducts(orderApprovalEventPayload.getProducts().stream()
                        .map(payloadProduct -> Product.newBuilder()
                                .setId(payloadProduct.getId())
                                .setQuantity(payloadProduct.getQuantity())
                                .build()).collect(Collectors.toList()))
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

    public CustomerModel customerAvroModelToCustomerModel(CustomerAvroModel customerAvroModel) {
        return CustomerModel.builder()
                .id(customerAvroModel.getId())
                .firstName(customerAvroModel.getFirstname())
                .lastName(customerAvroModel.getLastname())
                .username(customerAvroModel.getUsername())
                .build();
    }

}
