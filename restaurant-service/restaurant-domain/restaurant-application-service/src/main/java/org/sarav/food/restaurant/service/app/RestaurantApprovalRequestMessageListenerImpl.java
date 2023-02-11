package org.sarav.food.restaurant.service.app;

import lombok.extern.slf4j.Slf4j;
import org.sarav.food.restaurant.service.app.dto.RestaurantApprovalRequest;
import org.sarav.food.restaurant.service.app.ports.input.message.listener.RestaurantApprovalRequestMessageListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RestaurantApprovalRequestMessageListenerImpl implements RestaurantApprovalRequestMessageListener {

    private final RestaurantApprovalRequestHelper restaurantApprovalRequestHelper;

    public RestaurantApprovalRequestMessageListenerImpl(RestaurantApprovalRequestHelper
                                                                restaurantApprovalRequestHelper) {
        this.restaurantApprovalRequestHelper = restaurantApprovalRequestHelper;
    }

    @Override
    public void approveOrder(RestaurantApprovalRequest restaurantApprovalRequest) {
        restaurantApprovalRequestHelper.persistOrderApproval(restaurantApprovalRequest);
    }
}
