package org.sarav.food.order.service.app.ports.output.message.publisher.restaurantapproval;

import org.sarav.food.order.service.app.outbox.model.approval.OrderApprovalOutboxMessage;
import org.sarav.food.order.system.outbox.OutboxStatus;

import java.util.function.BiConsumer;

public interface RestaurantApprovalRequestMessagePublisher {

    void publish(OrderApprovalOutboxMessage orderApprovalOutboxMessage,
                 BiConsumer<OrderApprovalOutboxMessage, OutboxStatus> outboxCallback);

}
