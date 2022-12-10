package org.sarav.food.payment.service.domain.entity;

import org.sarav.food.order.system.domain.entity.AggregateRoot;
import org.sarav.food.order.system.domain.valueobjects.CustomerId;
import org.sarav.food.order.system.domain.valueobjects.Money;
import org.sarav.food.order.system.domain.valueobjects.OrderId;
import org.sarav.food.order.system.domain.valueobjects.PaymentStatus;
import org.sarav.food.payment.service.domain.valueobjects.PaymentId;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public class Payment extends AggregateRoot<PaymentId> {

    private OrderId orderId;
    private CustomerId customerId;
    private Money amount;
    private ZonedDateTime createdAt;
    private PaymentStatus paymentStatus;


    public void initializePayment() {
        setId(new PaymentId(UUID.randomUUID()));
        setCreatedAt(ZonedDateTime.now(ZoneId.of("UTC")));
    }

    public void validatePayment(List<String> failureMessages) {

        if (amount == null || !amount.isGreaterThanZero()) {
            failureMessages.add("Amount must be greater than zero for a payment");
        }

    }

    public void updateStatus(PaymentStatus paymentStatus) {
        this.setPaymentStatus(paymentStatus);
    }

    private Payment(Builder builder) {
        this.setId(builder.id);
        orderId = builder.orderId;
        customerId = builder.customerId;
        amount = builder.amount;
        createdAt = builder.createdAt;
        paymentStatus = builder.paymentStatus;
    }

    public OrderId getOrderId() {
        return orderId;
    }

    public void setOrderId(OrderId orderId) {
        this.orderId = orderId;
    }

    public CustomerId getCustomerId() {
        return customerId;
    }

    public void setCustomerId(CustomerId customerId) {
        this.customerId = customerId;
    }

    public Money getAmount() {
        return amount;
    }

    public void setAmount(Money amount) {
        this.amount = amount;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private PaymentId id;
        private OrderId orderId;
        private CustomerId customerId;
        private Money amount;
        private ZonedDateTime createdAt;
        private PaymentStatus paymentStatus;

        private Builder() {
        }

        public Builder id(PaymentId val) {
            id = val;
            return this;
        }

        public Builder orderId(OrderId val) {
            orderId = val;
            return this;
        }

        public Builder customerId(CustomerId val) {
            customerId = val;
            return this;
        }

        public Builder amount(Money val) {
            amount = val;
            return this;
        }

        public Builder createdAt(ZonedDateTime val) {
            createdAt = val;
            return this;
        }

        public Builder paymentStatus(PaymentStatus val) {
            paymentStatus = val;
            return this;
        }

        public Payment build() {
            return new Payment(this);
        }
    }
}
