package org.sarav.food.order.service.app.outbox.scheduler.approval;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.sarav.food.order.service.app.outbox.model.approval.OrderApprovalEventPayload;
import org.sarav.food.order.service.app.outbox.model.approval.OrderApprovalOutboxMessage;
import org.sarav.food.order.service.app.ports.output.repository.RestaurantApprovalOutboxRepository;
import org.sarav.food.order.service.domain.exception.OrderDomainException;
import org.sarav.food.order.system.domain.valueobjects.OrderStatus;
import org.sarav.food.order.system.outbox.OutboxStatus;
import org.sarav.food.order.system.saga.SagaStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.sarav.food.order.system.saga.order.SagaConstants.ORDER_SAGA_NAME;

@Slf4j
@Component
public class RestaurantApprovalOutboxHelper {

    private final RestaurantApprovalOutboxRepository restaurantApprovalOutboxRepository;
    private final ObjectMapper objectMapper;

    public RestaurantApprovalOutboxHelper(RestaurantApprovalOutboxRepository restaurantApprovalOutboxRepository, ObjectMapper objectMapper) {
        this.restaurantApprovalOutboxRepository = restaurantApprovalOutboxRepository;
        this.objectMapper = objectMapper;
    }


    @Transactional(readOnly = true)
    public Optional<List<OrderApprovalOutboxMessage>> getApprovalOutboxMessagesByOutboxStatusAndSagaStatus(
            OutboxStatus outboxStatus,
            SagaStatus... sagaStatuses) {
        return restaurantApprovalOutboxRepository.findByTypeAndOutboxStatusAndSagaStatusIn(ORDER_SAGA_NAME, outboxStatus, sagaStatuses);
    }

    @Transactional(readOnly = true)
    public Optional<OrderApprovalOutboxMessage> getApprovalOutboxMessageBySagaIdAndSagaStatus(UUID sagaId,
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

    @Transactional
    public void saveApprovalOutboxMessage(OrderApprovalEventPayload payload, OrderStatus orderStatus, SagaStatus sagaStatus,
                                          OutboxStatus outboxStatus,
                                          UUID sagaId) {
        save(OrderApprovalOutboxMessage.builder()
                .id(UUID.randomUUID())
                .sagaId(sagaId)
                .payload(createPayload(payload))
                .type(ORDER_SAGA_NAME)
                .createdAt(ZonedDateTime.now(ZoneId.of("UTC")))
                .processedAt(ZonedDateTime.now(ZoneId.of("UTC")))
                .orderStatus(orderStatus)
                .outboxStatus(outboxStatus)
                .sagaStatus(sagaStatus)
                .build());
    }

    private String createPayload(OrderApprovalEventPayload payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            log.error("Error Occurred when tried converting OrderApprovalEventPayload to JSON for Order {}", payload.getOrderId());
            throw new OrderDomainException("Error Occurred when tried converting OrderApprovalEventPayload to JSON for Order " + payload.getOrderId());
        }
    }

}
