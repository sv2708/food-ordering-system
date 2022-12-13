package org.sarav.food.payment.service.app.ports.output.message.publisher;

import org.sarav.food.order.system.domain.event.publisher.DomainEventPublisher;
import org.sarav.food.payment.service.domain.event.PaymentCancelledEvent;

public interface PaymentCancelledMessagePublisher extends DomainEventPublisher<PaymentCancelledEvent> {
}
