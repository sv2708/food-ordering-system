package org.sarav.food.order.service.domain;

import org.sarav.food.order.service.domain.entity.Order;
import org.sarav.food.order.service.domain.entity.Restaurant;
import org.sarav.food.order.service.domain.event.OrderCancelledEvent;
import org.sarav.food.order.service.domain.event.OrderCreatedEvent;
import org.sarav.food.order.service.domain.event.OrderPaidEvent;

import java.util.List;

public interface OrderDomainService {

    OrderCreatedEvent validateAndInitiateOrder(Order order, Restaurant restaurant);

    OrderPaidEvent payOrder(Order order);

    void approveOrder(Order order);

    OrderCancelledEvent cancelRejectedOrder(Order order, List<String> failureMessages);

    void cancelOrder(Order order, List<String> failureMessages);

}
