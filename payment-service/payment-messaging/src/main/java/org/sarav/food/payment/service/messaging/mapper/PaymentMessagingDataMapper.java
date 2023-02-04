package org.sarav.food.payment.service.messaging.mapper;

import org.sarav.food.order.OrderPaymentStatus;
import org.sarav.food.order.PaymentRequestAvroModel;
import org.sarav.food.order.PaymentResponseAvroModel;
import org.sarav.food.order.system.domain.valueobjects.PaymentOrderStatus;
import org.sarav.food.payment.service.app.dto.PaymentRequest;
import org.sarav.food.payment.service.app.outbox.model.OrderEventPayload;
import org.sarav.food.payment.service.domain.event.PaymentEvent;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PaymentMessagingDataMapper {

    public PaymentResponseAvroModel paymentEventToPaymentResponseAvroModel(PaymentEvent paymentEvent) {
        return PaymentResponseAvroModel.newBuilder().setId(UUID.randomUUID().toString())
                .setSagaId("")
                .setPaymentId(paymentEvent.getPayment().getId().getValue().toString())
                .setOrderId(paymentEvent.getPayment().getOrderId().getValue().toString())
                .setCustomerId(paymentEvent.getPayment().getCustomerId().getValue().toString())
                .setPrice(paymentEvent.getPayment().getAmount().getAmount())
                .setFailureMessages(paymentEvent.getFailureMessages())
                .setOrderPaymentStatus(OrderPaymentStatus.valueOf(paymentEvent.getPayment()
                        .getPaymentStatus().toString()))
                .setCreatedAt(paymentEvent.getCreatedAt().toInstant())
                .build();
    }

    public PaymentRequest paymentRequestAvroModelToPaymentRequest(PaymentRequestAvroModel paymentRequestAvroModel) {
        return PaymentRequest.builder()
                .id(paymentRequestAvroModel.getId())
                .sagaId(paymentRequestAvroModel.getSagaId())
                .customerId(paymentRequestAvroModel.getCustomerId())
                .OrderId(paymentRequestAvroModel.getOrderId())
                .paymentOrderStatus(PaymentOrderStatus.valueOf(paymentRequestAvroModel.getOrderPaymentStatus().toString()))
                .amount(paymentRequestAvroModel.getPrice())
                .createdAt(paymentRequestAvroModel.getCreatedAt())
                .build();
    }

    public PaymentResponseAvroModel orderEventPayloadToPaymentResponseAvroModel(String sagaId, OrderEventPayload orderEventPayload) {

        return PaymentResponseAvroModel.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setSagaId(sagaId)
                .setOrderId(orderEventPayload.getOrderId())
                .setPaymentId(orderEventPayload.getPaymentId())
                .setCustomerId(orderEventPayload.getCustomerId())
                .setPrice(orderEventPayload.getPrice())
                .setOrderPaymentStatus(OrderPaymentStatus.valueOf(orderEventPayload.getPaymentStatus()))
                .setCreatedAt(orderEventPayload.getCreatedAt().toInstant())
                .setFailureMessages(orderEventPayload.getFailureMessages())
                .build();

    }
}
