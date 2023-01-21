package org.sarav.food.order.service.app.outbox.scheduler.approval;

import lombok.extern.slf4j.Slf4j;
import org.sarav.food.order.service.app.outbox.model.approval.OrderApprovalOutboxMessage;
import org.sarav.food.order.service.app.ports.output.repository.RestaurantApprovalOutboxRepository;
import org.sarav.food.order.service.domain.exception.OrderDomainException;
import org.sarav.food.order.system.outbox.OutboxStatus;
import org.sarav.food.order.system.saga.SagaStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.sarav.food.order.system.saga.order.SagaConstants.ORDER_SAGA_NAME;

@Slf4j
@Component
public class RestaurantApprovalOutboxHelper {

    private final RestaurantApprovalOutboxRepository restaurantApprovalOutboxRepository;

    public RestaurantApprovalOutboxHelper(RestaurantApprovalOutboxRepository restaurantApprovalOutboxRepository) {
        this.restaurantApprovalOutboxRepository = restaurantApprovalOutboxRepository;
    }


    @Transactional(readOnly = true)
    public Optional<List<OrderApprovalOutboxMessage>> getApprovalOutboxMessagesByOutboxStatusAndSagaStatus(
            OutboxStatus outboxStatus,
            SagaStatus... sagaStatuses) {
        return restaurantApprovalOutboxRepository.findByTypeAndOutboxStatusAndSagaStatusIn(ORDER_SAGA_NAME, outboxStatus, sagaStatuses);
    }

    @Transactional(readOnly = true)
    public Optional<OrderApprovalOutboxMessage> getApprovalOutboxMessageBySagaIdAndSagaStatus(String type,
                                                                                              UUID sagaId,
                                                                                              SagaStatus sagaStatus) {
        return restaurantApprovalOutboxRepository.findByTypeAndSagaIdAndSagaStatus(ORDER_SAGA_NAME, sagaId, sagaStatus);
    }

    @Transactional
    public void save(OrderApprovalOutboxMessage orderApprovalOutboxMessage) {
        OrderApprovalOutboxMessage response = restaurantApprovalOutboxRepository.save(orderApprovalOutboxMessage);
        if (response == null) {
            log.error("Could not save the updated OrderApprovalOutboxMessage {}", orderApprovalOutboxMessage.getId());
            throw new OrderDomainException("Could not save the updated OrderApprovalOutboxMessage " + orderApprovalOutboxMessage.getId());
        }
        log.info("OrderApprovalOutboxMessage with Id {} has been successfully saved", orderApprovalOutboxMessage.getId());
    }

    @Transactional
    public void deleteApprovalOutboxMessageByOutboxStatusAndSagaStatus(OutboxStatus outboxStatus,
                                                                       SagaStatus... sagaStatuses) {
        restaurantApprovalOutboxRepository.deleteByTypeAndOutboxStatusAndSagaStatus(ORDER_SAGA_NAME, outboxStatus, sagaStatuses);
    }

}
