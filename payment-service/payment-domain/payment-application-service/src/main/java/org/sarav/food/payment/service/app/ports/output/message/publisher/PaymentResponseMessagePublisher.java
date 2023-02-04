package org.sarav.food.payment.service.app.ports.output.message.publisher;

import org.sarav.food.order.system.outbox.OutboxStatus;
import org.sarav.food.payment.service.app.outbox.model.OrderOutboxMessage;

import java.util.function.BiConsumer;

public interface PaymentResponseMessagePublisher {

    void publish(OrderOutboxMessage orderOutboxMessage,
                 BiConsumer<OrderOutboxMessage, OutboxStatus> outboxCallback);


}
