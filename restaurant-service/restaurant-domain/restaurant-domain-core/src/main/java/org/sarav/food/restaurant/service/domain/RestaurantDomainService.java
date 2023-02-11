package org.sarav.food.restaurant.service.domain;

import org.sarav.food.restaurant.service.domain.entity.Restaurant;
import org.sarav.food.restaurant.service.domain.event.OrderApprovalEvent;

import java.util.List;

public interface RestaurantDomainService {

    OrderApprovalEvent validateOrder(Restaurant restaurant,
                                     List<String> failureMessages);
}
