package org.sarav.food.restaurant.service.app.outbox.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.sarav.food.order.system.domain.valueobjects.OrderApprovalStatus;
import org.sarav.food.order.system.outbox.OutboxStatus;

import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class OrderOutboxMessage {
    private UUID id;
    private UUID sagaId;
    private ZonedDateTime createdAt;
    private ZonedDateTime processedAt;
    private String type;
    private String payload;
    private OutboxStatus outboxStatus;
    private OrderApprovalStatus approvalStatus;
    private int version;

    public void setOutboxStatus(OutboxStatus status) {
        this.outboxStatus = status;
    }
}
