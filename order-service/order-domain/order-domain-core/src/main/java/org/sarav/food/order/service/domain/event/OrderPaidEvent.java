package org.sarav.food.order.service.domain.event;

import org.sarav.food.order.service.domain.entity.Order;
import org.sarav.food.system.domain.event.DomainEvent;

import java.time.ZonedDateTime;

public class OrderPaidEvent extends OrderEvent {

    public OrderPaidEvent(Order order, ZonedDateTime createdAt) {
        super(order, createdAt);
    }

}
