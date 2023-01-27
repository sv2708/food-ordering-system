package org.sarav.food.order.service.domain.event;

import org.sarav.food.order.service.domain.entity.Order;

import java.time.ZonedDateTime;

/**
 * This event gets triggered after the Order gets cancelled after it gets rejected from the Restaurant
 */
public class OrderCancelledEvent extends OrderEvent {


    public OrderCancelledEvent(Order order, ZonedDateTime createdAt) {
        super(order, createdAt);
    }

}
