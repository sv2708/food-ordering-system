package org.sarav.food.restaurant.service.domain;

import org.sarav.food.order.system.domain.event.publisher.DomainEventPublisher;
import org.sarav.food.restaurant.service.domain.entity.Restaurant;
import org.sarav.food.restaurant.service.domain.event.OrderApprovalEvent;
import org.sarav.food.restaurant.service.domain.event.OrderApprovedEvent;
import org.sarav.food.restaurant.service.domain.event.OrderRejectedEvent;

import java.util.List;

public interface RestaurantDomainService {

    OrderApprovalEvent validateOrder(Restaurant restaurant,
                                     List<String> failureMessages,
                                     DomainEventPublisher<OrderApprovedEvent> orderApprovedEventDomainEventPublisher,
                                     DomainEventPublisher<OrderRejectedEvent> orderRejectedEventDomainEventPublisher);
}
