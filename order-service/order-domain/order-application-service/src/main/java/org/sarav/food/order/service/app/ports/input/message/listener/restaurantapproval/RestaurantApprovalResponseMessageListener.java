package org.sarav.food.order.service.app.ports.input.message.listener.restaurantapproval;

public interface RestaurantApprovalResponseMessageListener {

    void orderApproved(RestaurantApprovalResponseMessageListener restaurantApprovalResponse);

    void orderRejected(RestaurantApprovalResponseMessageListener restaurantApprovalResponse);
}
