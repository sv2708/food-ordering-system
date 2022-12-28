package org.sarav.food.order.service.domain;

import lombok.extern.slf4j.Slf4j;
import org.sarav.food.order.service.domain.entity.Order;
import org.sarav.food.order.service.domain.entity.Restaurant;
import org.sarav.food.order.service.domain.event.OrderCancelledEvent;
import org.sarav.food.order.service.domain.event.OrderCreatedEvent;
import org.sarav.food.order.service.domain.event.OrderPaidEvent;
import org.sarav.food.order.service.domain.exception.OrderDomainException;
import org.sarav.food.order.system.domain.DomainConstants;
import org.sarav.food.order.system.domain.event.publisher.DomainEventPublisher;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Slf4j
public class OrderDomainServiceImpl implements OrderDomainService {


    @Override
    public OrderCreatedEvent validateAndInitiateOrder(Order order,
                                                      Restaurant restaurant,
                                                      DomainEventPublisher<OrderCreatedEvent> orderCreatedEventPublisher) {
        validateRestaurant(restaurant);
        setOrderProductInformation(order, restaurant);
        order.validateOrder();
        order.initializeOrder();
        log.info("Order with id " + order.getId() + " has been initiated");
        return new OrderCreatedEvent(order, ZonedDateTime.now(ZoneId.of(DomainConstants.UTC)),
                orderCreatedEventPublisher);
    }

    @Override
    public OrderPaidEvent payOrder(Order order, DomainEventPublisher<OrderPaidEvent> orderPaidEventPublisher) {
        order.pay();
        log.info("Order with id {} has been paid", order.getId());
        return new OrderPaidEvent(order, ZonedDateTime.now(ZoneId.of(DomainConstants.UTC)), orderPaidEventPublisher);
    }

    @Override
    public void approveOrder(Order order) {
        order.approve();
        log.info("Order with id: {} has been approved", order.getId());
    }

    @Override
    public OrderCancelledEvent cancelOrderPayment(Order order, List<String> failureMessages,
                                                  DomainEventPublisher<OrderCancelledEvent> orderCancelledMessagePublisher) {
        order.initCancel(failureMessages);
        log.info("Cancel initiated for Order with id " + order.getId());
        return new OrderCancelledEvent(order, ZonedDateTime.now(ZoneId.of(DomainConstants.UTC)), orderCancelledMessagePublisher);
    }

    @Override
    public void cancelOrder(Order order, List<String> failureMessages) {
        order.cancel(failureMessages);
        log.info("Order with id {} has been cancelled", order.getId());
    }

    private void validateRestaurant(Restaurant restaurant) {
        if (!restaurant.isActive()) {
            throw new OrderDomainException("Restaurant with id " + restaurant.getId().getValue() + " is not currently active");
        }
    }

    /**
     * Checks if product in each order item is same as in restaurant list
     * and set its current price and name
     *
     * @param order
     * @param restaurant
     */
    private void setOrderProductInformation(Order order, Restaurant restaurant) {
        order.getItems().forEach(orderItem -> {
            restaurant.getProductList().forEach(restaurantProduct -> {
                var currentProduct = orderItem.getProduct();
                if (restaurantProduct.equals(currentProduct)) {
                    currentProduct.updateWithCurrentNameAndPrice(restaurantProduct.getName(), restaurantProduct.getPrice());
                }
            });
        });
    }

}
