package org.sarav.food.payment.service.app.outbox.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.sarav.food.order.system.outbox.OutboxScheduler;
import org.sarav.food.order.system.outbox.OutboxStatus;
import org.sarav.food.payment.service.app.outbox.OrderOutboxHelper;
import org.sarav.food.payment.service.app.outbox.model.OrderOutboxMessage;
import org.sarav.food.payment.service.app.ports.output.message.publisher.PaymentResponseMessagePublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class OrderOutboxScheduler implements OutboxScheduler {

    private final OrderOutboxHelper orderOutboxHelper;
    private final PaymentResponseMessagePublisher paymentResponseMessagePublisher;

    public OrderOutboxScheduler(OrderOutboxHelper orderOutboxHelper,
                                PaymentResponseMessagePublisher paymentResponseMessagePublisher) {
        this.orderOutboxHelper = orderOutboxHelper;
        this.paymentResponseMessagePublisher = paymentResponseMessagePublisher;
    }

    @Override
    @Transactional
    @Scheduled(fixedDelayString = "${payment-service.outbox-scheduler-fixed-rate}",
            initialDelayString = "${payment-service.outbox-scheduler-initial-delay}")
    public void processOutboxMessage() {
        Optional<List<OrderOutboxMessage>> outboxMessages = orderOutboxHelper.getOrderOutboxMessagebyOutboxStatus(OutboxStatus.STARTED);
        if (outboxMessages.isPresent() && outboxMessages.get().size() > 0) {
            log.info("Publishing {} outbox messages to the response topic in Kafka. IDs: {}",
                    outboxMessages.get().size(),
                    outboxMessages.get().stream().map(msg -> msg.getId().toString()).collect(Collectors.joining("")));
            outboxMessages.get().stream().forEach(message -> {
                paymentResponseMessagePublisher.publish(message, orderOutboxHelper::updateOutboxMessageStatus);
                log.info("OrderOutboxMessage with Saga {} has been published to message bus", message.getSagaId());
            });
        }
    }
}
