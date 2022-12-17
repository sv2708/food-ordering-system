package org.sarav.food.payment.service.messaging.mapper;

import org.sarav.food.order.OrderPaymentStatus;
import org.sarav.food.order.PaymentRequestAvroModel;
import org.sarav.food.order.PaymentResponseAvroModel;
import org.sarav.food.payment.service.app.dto.PaymentRequest;
import org.sarav.food.payment.service.domain.event.PaymentEvent;
import org.sarav.food.payment.service.domain.valueobjects.PaymentOrderStatus;
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

}
