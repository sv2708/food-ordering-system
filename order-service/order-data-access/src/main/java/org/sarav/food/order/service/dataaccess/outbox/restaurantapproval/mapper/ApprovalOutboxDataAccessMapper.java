package org.sarav.food.order.service.dataaccess.outbox.restaurantapproval.mapper;

import org.sarav.food.order.service.app.outbox.model.approval.OrderApprovalOutboxMessage;
import org.sarav.food.order.service.dataaccess.outbox.restaurantapproval.entity.ApprovalOutboxEntity;
import org.springframework.stereotype.Component;

@Component
public class ApprovalOutboxDataAccessMapper {

    public ApprovalOutboxEntity orderCreatedOutboxMessageToOutboxEntity(OrderApprovalOutboxMessage
                                                                                orderApprovalOutboxMessage) {
        return ApprovalOutboxEntity.builder()
                .id(orderApprovalOutboxMessage.getId())
                .sagaId(orderApprovalOutboxMessage.getSagaId())
                .createdAt(orderApprovalOutboxMessage.getCreatedAt())
                .processedAt(orderApprovalOutboxMessage.getProcessedAt())
                .type(orderApprovalOutboxMessage.getType())
                .payload(orderApprovalOutboxMessage.getPayload())
                .orderStatus(orderApprovalOutboxMessage.getOrderStatus())
                .sagaStatus(orderApprovalOutboxMessage.getSagaStatus())
                .outboxStatus(orderApprovalOutboxMessage.getOutboxStatus())
                .version(orderApprovalOutboxMessage.getVersion())
                .build();
    }

    public OrderApprovalOutboxMessage approvalOutboxEntityToOrderApprovalOutboxMessage(ApprovalOutboxEntity
                                                                                               approvalOutboxEntity) {
        return OrderApprovalOutboxMessage.builder()
                .id(approvalOutboxEntity.getId())
                .sagaId(approvalOutboxEntity.getSagaId())
                .createdAt(approvalOutboxEntity.getCreatedAt())
                .processedAt(approvalOutboxEntity.getProcessedAt())
                .type(approvalOutboxEntity.getType())
                .payload(approvalOutboxEntity.getPayload())
                .orderStatus(approvalOutboxEntity.getOrderStatus())
                .sagaStatus(approvalOutboxEntity.getSagaStatus())
                .outboxStatus(approvalOutboxEntity.getOutboxStatus())
                .version(approvalOutboxEntity.getVersion())
                .build();
    }

}
