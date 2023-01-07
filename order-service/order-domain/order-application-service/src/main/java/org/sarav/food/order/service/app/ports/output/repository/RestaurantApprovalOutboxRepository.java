package org.sarav.food.order.service.app.ports.output.repository;


import org.sarav.food.order.service.app.outbox.model.approval.OrderApprovalOutboxMessage;
import org.sarav.food.order.system.outbox.OutboxStatus;
import org.sarav.food.order.system.saga.SagaStatus;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RestaurantApprovalOutboxRepository {

    Optional<OrderApprovalOutboxMessage> findByTypeAndOutboxStatusAndSagaStatus(String type,
                                                                                OutboxStatus outboxStatus,
                                                                                SagaStatus sagaStatus);

    Optional<OrderApprovalOutboxMessage> findByTypeAndSagaIdAndSagaStatus(String type,
                                                                          UUID sagaId,
                                                                          SagaStatus sagaStatus);

    void deleteByTypeAndOutboxStatusAndSagaStatus(String type,
                                                  OutboxStatus outboxStatus,
                                                  SagaStatus sagaStatus);


}
