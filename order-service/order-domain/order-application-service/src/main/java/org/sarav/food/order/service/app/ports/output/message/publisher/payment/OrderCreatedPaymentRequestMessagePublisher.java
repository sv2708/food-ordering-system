package org.sarav.food.order.service.app.ports.output.message.publisher.payment;

import org.sarav.food.order.service.domain.event.OrderCreatedEvent;
import org.sarav.food.system.domain.event.publisher.DomainEventPublisher;

public interface OrderCreatedPaymentRequestMessagePublisher extends DomainEventPublisher<OrderCreatedEvent> {



}
