package org.sarav.food.payment.service.app.ports.output.message.publisher;

import org.sarav.food.order.system.domain.event.publisher.DomainEventPublisher;
import org.sarav.food.payment.service.domain.event.PaymentCompletedEvent;

public interface PaymentCompletedMessagePublisher extends DomainEventPublisher<PaymentCompletedEvent> {
}
