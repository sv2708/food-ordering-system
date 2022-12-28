package org.sarav.food.payment.service.dataaccess.payment.mapper;

import org.sarav.food.order.system.domain.valueobjects.CustomerId;
import org.sarav.food.order.system.domain.valueobjects.Money;
import org.sarav.food.order.system.domain.valueobjects.OrderId;
import org.sarav.food.payment.service.dataaccess.payment.entity.PaymentEntity;
import org.sarav.food.payment.service.domain.entity.Payment;
import org.sarav.food.payment.service.domain.valueobjects.PaymentId;
import org.springframework.stereotype.Component;

@Component
public class PaymentDataAccessMapper {

    public PaymentEntity paymentToPaymentEntity(Payment payment) {
        return PaymentEntity.builder()
                .id(payment.getId().getValue())
                .customerId(payment.getCustomerId().getValue())
                .orderId(payment.getOrderId().getValue())
                .amount(payment.getAmount().getAmount())
                .status(payment.getPaymentStatus())
                .createdAt(payment.getCreatedAt())
                .build();
    }

    public Payment paymentEntityToPayment(PaymentEntity paymentEntity) {
        return Payment.builder()
                .id(new PaymentId(paymentEntity.getId()))
                .customerId(new CustomerId(paymentEntity.getCustomerId()))
                .orderId(new OrderId(paymentEntity.getOrderId()))
                .amount(new Money(paymentEntity.getAmount()))
                .createdAt(paymentEntity.getCreatedAt())
                .build();
    }

}
