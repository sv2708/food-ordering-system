package org.sarav.food.order.service.app.outbox.scheduler.approval;

import lombok.extern.slf4j.Slf4j;
import org.sarav.food.order.service.app.outbox.model.approval.OrderApprovalOutboxMessage;
import org.sarav.food.order.service.app.ports.output.message.publisher.restaurantapproval.RestaurantApprovalRequestMessagePublisher;
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
public class RestaurantApprovalOutboxScheduler implements OutboxScheduler {

    private final RestaurantApprovalOutboxHelper restaurantApprovalOutboxHelper;
    private final RestaurantApprovalRequestMessagePublisher restaurantApprovalRequestMessagePublisher;

    public RestaurantApprovalOutboxScheduler(RestaurantApprovalOutboxHelper restaurantApprovalOutboxHelper, RestaurantApprovalRequestMessagePublisher restaurantApprovalRequestMessagePublisher) {
        this.restaurantApprovalOutboxHelper = restaurantApprovalOutboxHelper;
        this.restaurantApprovalRequestMessagePublisher = restaurantApprovalRequestMessagePublisher;
    }

    @Override
    @Transactional
    @Scheduled(fixedDelayString = "${order-service.outbox-scheduler-fixed-delay}",
            initialDelayString = "${order-service.outbox-scheduler-initial-delay}")
    public void processOutboxMessage() {
        log.info("Pulling Outbox messages from Restaurant Approval Outbox table with Status {} from the table", OutboxStatus.STARTED);
        Optional<List<OrderApprovalOutboxMessage>> outboxMessages = restaurantApprovalOutboxHelper
                .getApprovalOutboxMessagesByOutboxStatusAndSagaStatus(OutboxStatus.STARTED, SagaStatus.STARTED, SagaStatus.COMPENSATING);

        if (outboxMessages.isPresent()) {
            List<OrderApprovalOutboxMessage> messages = outboxMessages.get();
            log.info("Received {} Order Approval messages from the Restaurant Approval outbox table. Send messages with ids {} to the message bus",
                    messages.stream().map(msg -> msg.getId().toString()).collect(Collectors.joining(","))
            );
            messages.stream().forEach(message -> {
                restaurantApprovalRequestMessagePublisher.publish(message, this::updateOutboxStatus);
            });
            log.info("Successfully Published {} Order Approval Messages to the message bus", messages.size());
        } else {
            log.info("No Order Approval Messages are currently available in the outbox table to publish");
        }
    }

    private void updateOutboxStatus(OrderApprovalOutboxMessage outboxMessage, OutboxStatus outboxStatus) {
        outboxMessage.setOutboxStatus(outboxStatus);
        restaurantApprovalOutboxHelper.save(outboxMessage);
        log.info("Order Approval Outbox Message with id {} has been successfully updated with status {}", outboxMessage.getId(),
                outboxStatus.toString());
    }

}
