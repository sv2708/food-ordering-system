package org.sarav.food.order.service.domain.event;

import org.sarav.food.order.service.domain.entity.Order;
import org.sarav.food.order.system.domain.event.publisher.DomainEventPublisher;

import java.time.ZonedDateTime;

public class OrderCancelledEvent extends OrderEvent {

    private final DomainEventPublisher<OrderCancelledEvent> orderCancelledMessagePublisher;

    public OrderCancelledEvent(Order order, ZonedDateTime createdAt,
                               DomainEventPublisher<OrderCancelledEvent> orderCancelledMessagePublisher) {
        super(order, createdAt);
        this.orderCancelledMessagePublisher = orderCancelledMessagePublisher;
    }

    @Override
    public void fire() {
        orderCancelledMessagePublisher.publish(this);
    }
}
