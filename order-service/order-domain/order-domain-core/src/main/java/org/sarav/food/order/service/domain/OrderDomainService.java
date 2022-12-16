package org.sarav.food.order.service.domain;

import org.sarav.food.order.service.domain.entity.Order;
import org.sarav.food.order.service.domain.entity.Restaurant;
import org.sarav.food.order.service.domain.event.OrderCancelledEvent;
import org.sarav.food.order.service.domain.event.OrderCreatedEvent;
import org.sarav.food.order.service.domain.event.OrderPaidEvent;
import org.sarav.food.order.system.domain.event.publisher.DomainEventPublisher;

import java.util.List;

public interface OrderDomainService {

    OrderCreatedEvent validateAndInitiateOrder(Order order, Restaurant restaurant, DomainEventPublisher<OrderCreatedEvent> orderCreatedEventPublisher);

    OrderPaidEvent payOrder(Order order, DomainEventPublisher<OrderPaidEvent> orderPaidEventPublisher);

    void approveOrder(Order order);

    OrderCancelledEvent cancelOrderPayment(Order order, List<String> failureMessages, DomainEventPublisher<OrderCancelledEvent> orderCancelledMessagePublisher);

    void cancelOrder(Order order, List<String> failureMessages);

}
