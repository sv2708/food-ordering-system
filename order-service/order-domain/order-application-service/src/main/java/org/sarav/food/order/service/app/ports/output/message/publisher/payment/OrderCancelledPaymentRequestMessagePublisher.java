package org.sarav.food.order.service.app.ports.output.message.publisher.payment;

import org.sarav.food.order.service.domain.event.OrderCancelledEvent;
import org.sarav.food.system.domain.event.publisher.DomainEventPublisher;

public interface OrderCancelledPaymentRequestMessagePublisher extends DomainEventPublisher<OrderCancelledEvent> {
}
