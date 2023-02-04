package org.sarav.food.payment.service.app.outbox.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.sarav.food.order.system.outbox.OutboxScheduler;
import org.sarav.food.order.system.outbox.OutboxStatus;
import org.sarav.food.payment.service.app.outbox.OrderOutboxHelper;
import org.sarav.food.payment.service.app.outbox.model.OrderOutboxMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class OrderOutboxCleanerScheduler implements OutboxScheduler {

    private OrderOutboxHelper orderOutboxHelper;

    @Transactional
    @Scheduled(cron = "@midnight")
    @Override
    public void processOutboxMessage() {

        Optional<List<OrderOutboxMessage>> outboxMessages =
                orderOutboxHelper.getOrderOutboxMessagebyOutboxStatus(OutboxStatus.COMPLETED);

        if (outboxMessages.isPresent() && outboxMessages.get().size() > 0) {
            List<OrderOutboxMessage> messages = outboxMessages.get();
            log.info("Received {} messages with Completed Status", messages.size());
            orderOutboxHelper.deleteByOutboxStatus(OutboxStatus.COMPLETED);
        }

    }
}
