package org.sarav.food.payment.service.domain.event;

import org.sarav.food.order.system.domain.event.publisher.DomainEventPublisher;
import org.sarav.food.payment.service.domain.entity.Payment;

import java.time.ZonedDateTime;
import java.util.List;

public class PaymentCompletedEvent extends PaymentEvent {

    private final DomainEventPublisher<PaymentCompletedEvent> paymentCompletedEventPublisher;

    public PaymentCompletedEvent(Payment payment, ZonedDateTime createdAt, List<String> failureMessages,
                                 DomainEventPublisher<PaymentCompletedEvent> paymentCompletedEventPublisher) {
        super(payment, createdAt, failureMessages);
        this.paymentCompletedEventPublisher = paymentCompletedEventPublisher;
    }

    @Override
    public void fire() {
        paymentCompletedEventPublisher.publish(this);
    }
}
