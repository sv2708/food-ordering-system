package org.sarav.food.order.service.app.ports.output.message.publisher.payment;

import org.sarav.food.order.service.app.outbox.model.payment.OrderPaymentOutboxMessage;
import org.sarav.food.order.system.outbox.OutboxStatus;

import java.util.function.BiConsumer;

public interface PaymentRequestMessagePublisher {

    void publish(OrderPaymentOutboxMessage orderPaymentOutboxMessage,
                 BiConsumer<OrderPaymentOutboxMessage, OutboxStatus> outboxCallback);

}
