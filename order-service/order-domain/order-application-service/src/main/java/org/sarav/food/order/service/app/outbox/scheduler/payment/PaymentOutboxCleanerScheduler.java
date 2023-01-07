package org.sarav.food.order.service.app.outbox.scheduler.payment;


import lombok.extern.slf4j.Slf4j;
import org.sarav.food.order.service.app.outbox.model.payment.OrderPaymentOutboxMessage;
import org.sarav.food.order.system.outbox.OutboxScheduler;
import org.sarav.food.order.system.outbox.OutboxStatus;
import org.sarav.food.order.system.saga.SagaStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class PaymentOutboxCleanerScheduler implements OutboxScheduler {

    private final PaymentOutboxHelper paymentOutboxHelper;

    public PaymentOutboxCleanerScheduler(PaymentOutboxHelper paymentOutboxHelper) {
        this.paymentOutboxHelper = paymentOutboxHelper;
    }

    @Override
    @Transactional
    @Scheduled(cron = "@midnight")
    public void processOutboxMessage() {
        log.info("Starting Cleanup of Completed Outbox messages");
        Optional<List<OrderPaymentOutboxMessage>> orderPaymentOutboxMessages =
                paymentOutboxHelper.getPaymentOutboxMessagesByOutboxStatusAndSagaStatus(OutboxStatus.COMPLETED,
                        SagaStatus.COMPENSATED, SagaStatus.FAILED, SagaStatus.SUCCEEDED);
        if (orderPaymentOutboxMessages.isPresent()) {
            List<OrderPaymentOutboxMessage> messages = orderPaymentOutboxMessages.get();
            log.info("Received {} messages to be cleaned up. Payload: {}", messages.size(),
                    messages.stream().map(msg -> msg.getPayload()).collect(Collectors.joining("\n")));
            paymentOutboxHelper.deletePaymentOutboxMessageByOutboxStatusAndSagaStatus(OutboxStatus.COMPLETED,
                    SagaStatus.COMPENSATED, SagaStatus.FAILED, SagaStatus.SUCCEEDED);
            log.info("Successfully deleted {} messages from the outbox table", messages.size());
        } else {
            log.info("No messages in the outbox table are available for the cleanup");
        }
    }
}
