package org.sarav.food.payment.service.domain.event;

import org.sarav.food.order.system.domain.event.publisher.DomainEventPublisher;
import org.sarav.food.payment.service.domain.entity.Payment;

import java.time.ZonedDateTime;
import java.util.List;

public class PaymentCancelledEvent extends PaymentEvent {
    private final DomainEventPublisher<PaymentCancelledEvent> paymentCancelledEventPublisher;

    public PaymentCancelledEvent(Payment payment, ZonedDateTime createdAt, List<String> failureMessages,
                                 DomainEventPublisher<PaymentCancelledEvent> paymentCancelledEventPublisher) {
        super(payment, createdAt, failureMessages);
        this.paymentCancelledEventPublisher = paymentCancelledEventPublisher;
    }

    @Override
    public void fire() {
        paymentCancelledEventPublisher.publish(this);
    }
}
