package org.sarav.food.order.service.app;

import lombok.extern.slf4j.Slf4j;
import org.sarav.food.order.service.app.dto.message.RestaurantApprovalResponse;
import org.sarav.food.order.service.app.ports.input.message.listener.restaurantapproval.RestaurantApprovalResponseMessageListener;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import static org.sarav.food.order.service.domain.entity.Order.FAILURE_MSG_DELIMITER;

@Component
@Slf4j
@Validated
public class RestaurantApprovalResponseMessageListenerImpl implements RestaurantApprovalResponseMessageListener {

    private final OrderApprovalSagaStep orderApprovalSagaStep;

    public RestaurantApprovalResponseMessageListenerImpl(OrderApprovalSagaStep orderApprovalSagaStep) {
        this.orderApprovalSagaStep = orderApprovalSagaStep;
    }

    @Override
    public void orderApproved(RestaurantApprovalResponse restaurantApprovalResponse) {
        orderApprovalSagaStep.success(restaurantApprovalResponse);
        log.info("Order {} has been placed successfully", restaurantApprovalResponse.getOrderId());
    }

    @Override
    public void orderRejected(RestaurantApprovalResponse restaurantApprovalResponse) {
        log.info("Persisting Order Cancelled Info for Order {}", restaurantApprovalResponse.getOrderId());
        orderApprovalSagaStep.rollback(restaurantApprovalResponse);
        log.info("Order Payment changes are rolled back to refund the payment for order {}",
                restaurantApprovalResponse.getOrderId());
        log.info("Failure Messages for Order {} are {}", restaurantApprovalResponse.getOrderId(),
                String.join(FAILURE_MSG_DELIMITER, restaurantApprovalResponse.getFailureMessages()));
    }
}
