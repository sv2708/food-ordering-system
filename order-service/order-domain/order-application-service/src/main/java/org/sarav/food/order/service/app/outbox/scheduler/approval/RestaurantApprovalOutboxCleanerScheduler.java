package org.sarav.food.order.service.app.outbox.scheduler.approval;

import lombok.extern.slf4j.Slf4j;
import org.sarav.food.order.service.app.outbox.model.approval.OrderApprovalOutboxMessage;
import org.sarav.food.order.system.outbox.OutboxScheduler;
import org.sarav.food.order.system.outbox.OutboxStatus;
import org.sarav.food.order.system.saga.SagaStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class RestaurantApprovalOutboxCleanerScheduler implements OutboxScheduler {

    private final RestaurantApprovalOutboxHelper restaurantApprovalOutboxHelper;

    public RestaurantApprovalOutboxCleanerScheduler(RestaurantApprovalOutboxHelper restaurantApprovalOutboxHelper) {
        this.restaurantApprovalOutboxHelper = restaurantApprovalOutboxHelper;
    }

    @Override
    public void processOutboxMessage() {
        log.info("Starting Cleanup of Completed Outbox messages of RestaurantApproval");
        Optional<List<OrderApprovalOutboxMessage>> orderApprovalOutboxMessages =
                restaurantApprovalOutboxHelper.getApprovalOutboxMessagesByOutboxStatusAndSagaStatus(OutboxStatus.COMPLETED,
                        SagaStatus.COMPENSATED, SagaStatus.FAILED, SagaStatus.SUCCEEDED);
        if (orderApprovalOutboxMessages.isPresent()) {
            List<OrderApprovalOutboxMessage> messages = orderApprovalOutboxMessages.get();
            log.info("Received {} RestaurantApproval messages to be cleaned up. Payload: {}", messages.size(),
                    messages.stream().map(msg -> msg.getPayload()).collect(Collectors.joining("\n")));
            restaurantApprovalOutboxHelper.deleteApprovalOutboxMessageByOutboxStatusAndSagaStatus(OutboxStatus.COMPLETED,
                    SagaStatus.COMPENSATED, SagaStatus.FAILED, SagaStatus.SUCCEEDED);
            log.info("Successfully deleted {} RestaurantApproval messages from the outbox table", messages.size());
        } else {
            log.info("No RestaurantApproval messages in the outbox table are available for the cleanup");
        }
    }
}
