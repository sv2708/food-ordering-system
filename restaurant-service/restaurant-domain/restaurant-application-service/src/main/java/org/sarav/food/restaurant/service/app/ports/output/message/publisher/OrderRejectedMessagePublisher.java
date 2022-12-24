package org.sarav.food.restaurant.service.app.ports.output.message.publisher;


import org.sarav.food.order.system.domain.event.publisher.DomainEventPublisher;
import org.sarav.food.restaurant.service.domain.event.OrderRejectedEvent;

public interface OrderRejectedMessagePublisher extends DomainEventPublisher<OrderRejectedEvent> {
}
