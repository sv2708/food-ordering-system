package org.sarav.food.restaurant.service.app.ports.output.message.publisher;

import org.sarav.food.order.system.outbox.OutboxStatus;
import org.sarav.food.restaurant.service.app.outbox.model.OrderOutboxMessage;

import java.util.function.BiConsumer;

public interface RestaurantApprovalResponseMessagePublisher {

    void publish(OrderOutboxMessage orderOutboxMessage,
                 BiConsumer<OrderOutboxMessage, OutboxStatus> outboxCallback);
    
}
