package org.sarav.food.restaurant.service.app.ports.input.message.listener;

import org.sarav.food.restaurant.service.app.dto.RestaurantApprovalRequest;

public interface RestaurantApprovalRequestMessageListener {
    void approveOrder(RestaurantApprovalRequest restaurantApprovalRequest);
}
