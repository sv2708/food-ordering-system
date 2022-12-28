package org.sarav.food.payment.service.app.mapper;

import org.sarav.food.order.system.domain.valueobjects.CustomerId;
import org.sarav.food.order.system.domain.valueobjects.Money;
import org.sarav.food.order.system.domain.valueobjects.OrderId;
import org.sarav.food.order.system.domain.valueobjects.PaymentStatus;
import org.sarav.food.payment.service.app.dto.PaymentRequest;
import org.sarav.food.payment.service.domain.entity.Payment;
import org.sarav.food.payment.service.domain.valueobjects.PaymentId;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.UUID;

@Component
public class PaymentDataMapper {

    public Payment paymentRequestToPayment(PaymentRequest paymentRequest) {
        return Payment.builder()
                .id(new PaymentId(UUID.fromString(paymentRequest.getId())))
                .orderId(new OrderId(UUID.fromString(paymentRequest.getOrderId())))
                .customerId(new CustomerId(UUID.fromString(paymentRequest.getCustomerId())))
                .paymentStatus(PaymentStatus.valueOf(paymentRequest.getPaymentOrderStatus().toString()))
                .amount(new Money(paymentRequest.getAmount()))
                .createdAt(paymentRequest.getCreatedAt().atZone(ZoneId.of("UTC")))
                .build();
    }


}
