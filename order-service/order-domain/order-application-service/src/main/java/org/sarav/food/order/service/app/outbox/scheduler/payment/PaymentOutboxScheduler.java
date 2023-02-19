package org.sarav.food.order.service.app.outbox.scheduler.payment;

import lombok.extern.slf4j.Slf4j;
import org.sarav.food.order.service.app.outbox.model.payment.OrderPaymentOutboxMessage;
import org.sarav.food.order.service.app.ports.output.message.publisher.payment.PaymentRequestMessagePublisher;
import org.sarav.food.order.system.outbox.OutboxScheduler;
import org.sarav.food.order.system.outbox.OutboxStatus;
import org.sarav.food.order.system.saga.SagaStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Slf4j
public class PaymentOutboxScheduler implements OutboxScheduler {

    private final PaymentOutboxHelper paymentOutboxHelper;
    private final PaymentRequestMessagePublisher paymentRequestMessagePublisher;

    public PaymentOutboxScheduler(PaymentOutboxHelper paymentOutboxHelper,
                                  PaymentRequestMessagePublisher paymentRequestMessagePublisher) {
        this.paymentOutboxHelper = paymentOutboxHelper;
        this.paymentRequestMessagePublisher = paymentRequestMessagePublisher;
    }

    @Override
    @Transactional
    @Scheduled(fixedDelayString = "${order-service.outbox-scheduler-fixed-delay}",
            initialDelayString = "${order-service.outbox-scheduler-initial-delay}")
    public void processOutboxMessage() {
        log.info("Pulling  Outbox messages from Payment Outbox table with Status {} from the table", OutboxStatus.STARTED);
        Optional<List<OrderPaymentOutboxMessage>> outboxMessages = paymentOutboxHelper
                .getPaymentOutboxMessagesByOutboxStatusAndSagaStatus(OutboxStatus.STARTED, SagaStatus.STARTED, SagaStatus.COMPENSATING);

        if (outboxMessages.isPresent() && outboxMessages.get().size() > 0) {
            List<OrderPaymentOutboxMessage> messages = outboxMessages.get();
            log.info("Received {} messages from the Payment outbox table. Send messages with ids {} to the message bus",
                    messages.stream().map(msg -> msg.getId().toString()).collect(Collectors.joining(",")));
            messages.stream().forEach(message -> {
                paymentRequestMessagePublisher.publish(message, this::updateOutboxStatus);
            });
            log.info("Successfully Published {} PaymentRequest Messages to the message bus", messages.size());
        } else {
            log.info("No PaymentRequest Messages are currently available in the outbox table to publish");
        }
    }

    private void updateOutboxStatus(OrderPaymentOutboxMessage outboxMessage, OutboxStatus outboxStatus) {
        outboxMessage.setOutboxStatus(outboxStatus);
        paymentOutboxHelper.save(outboxMessage);
        log.info("PaymentRequest Outbox Message with id {} has been successfully updated with status {}", outboxMessage.getId(), outboxStatus.toString());
    }

}
