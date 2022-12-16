package org.sarav.food.payment.service.domain.event;

import org.sarav.food.order.system.domain.event.publisher.DomainEventPublisher;
import org.sarav.food.payment.service.domain.entity.Payment;

import java.time.ZonedDateTime;
import java.util.List;

public class PaymentFailedEvent extends PaymentEvent {
    private final DomainEventPublisher<PaymentFailedEvent> paymentFailedEventPublisher;

    public PaymentFailedEvent(Payment payment, ZonedDateTime createdAt, List<String> failureMessages,
                              DomainEventPublisher<PaymentFailedEvent> paymentFailedEventPublisher) {
        super(payment, createdAt, failureMessages);
        this.paymentFailedEventPublisher = paymentFailedEventPublisher;
    }

    @Override
    public void fire() {
        paymentFailedEventPublisher.publish(this);
    }
}
