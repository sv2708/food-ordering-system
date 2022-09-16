package org.sarav.food.order.service.app.ports.input.message.listener.restaurantapproval;

import org.sarav.food.order.service.app.dto.message.RestaurantApprovalResponse;

public interface RestaurantApprovalResponseMessageListener {

    void orderApproved(RestaurantApprovalResponse restaurantApprovalResponse);

    void orderRejected(RestaurantApprovalResponse restaurantApprovalResponse);
}
