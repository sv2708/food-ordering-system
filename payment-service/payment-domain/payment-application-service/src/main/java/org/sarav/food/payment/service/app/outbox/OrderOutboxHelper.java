package org.sarav.food.payment.service.app.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.sarav.food.order.system.domain.valueobjects.PaymentStatus;
import org.sarav.food.order.system.outbox.OutboxStatus;
import org.sarav.food.payment.service.app.outbox.model.OrderEventPayload;
import org.sarav.food.payment.service.app.outbox.model.OrderOutboxMessage;
import org.sarav.food.payment.service.app.ports.output.repository.OrderOutboxRepository;
import org.sarav.food.payment.service.domain.exception.PaymentDomainException;
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
public class OrderOutboxHelper {

    private final OrderOutboxRepository orderOutboxRepository;
    private final ObjectMapper objectMapper;

    public OrderOutboxHelper(OrderOutboxRepository orderOutboxRepository,
                             ObjectMapper objectMapper) {
        this.orderOutboxRepository = orderOutboxRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional(readOnly = true)
    public Optional<OrderOutboxMessage> getCompletedOrderOutboxMessageBySagaIdAndPaymentStatus(UUID sagaId,
                                                                                               PaymentStatus paymentStatus) {
        return orderOutboxRepository.findByTypeAndSagaIdAndPaymentStatusAndOutboxStatus(ORDER_SAGA_NAME, sagaId,
                paymentStatus, OutboxStatus.COMPLETED);
    }

    @Transactional(readOnly = true)
    public Optional<List<OrderOutboxMessage>> getOrderOutboxMessagebyOutboxStatus(OutboxStatus outboxStatus) {
        return orderOutboxRepository.findByTypeAndOutboxStatus(ORDER_SAGA_NAME, outboxStatus);
    }

    @Transactional
    public void deleteByOutboxStatus(OutboxStatus outboxStatus) {
        orderOutboxRepository.deleteByTypeAndOutboxStatus(ORDER_SAGA_NAME, outboxStatus);
    }

    @Transactional
    public void saveOrderOutboxMessage(OrderEventPayload orderEventPayload,
                                       PaymentStatus paymentStatus,
                                       OutboxStatus outboxStatus, UUID sagaId) {
        save(OrderOutboxMessage.builder()
                .id(UUID.randomUUID())
                .payload(createPayload(orderEventPayload))
                .type(ORDER_SAGA_NAME)
                .outboxStatus(outboxStatus)
                .paymentStatus(paymentStatus)
                .sagaId(sagaId)
                .createdAt(orderEventPayload.getCreatedAt())
                .processedAt(ZonedDateTime.now(ZoneId.of("UTC")))
                .build());
    }

    private String createPayload(OrderEventPayload orderEventPayload) {
        try {
            return objectMapper.writeValueAsString(orderEventPayload);
        } catch (JsonProcessingException e) {
            log.error("Error Converting OrderEventPayload to JSON String {}", e.getMessage());
            throw new PaymentDomainException("Error Converting OrderEventPayload to JSON String");
        }
    }

    @Transactional
    public void updateOutboxMessageStatus(OrderOutboxMessage outboxMessage, OutboxStatus outboxStatus) {
        log.info("Updating OrderOutboxStatus to {} for OutboxMessage {}", outboxStatus.name(), outboxMessage.getId());
        outboxMessage.setOutboxStatus(outboxStatus);
        save(outboxMessage);
    }

    private void save(OrderOutboxMessage orderOutboxMessage) {
        OrderOutboxMessage response = orderOutboxRepository.save(orderOutboxMessage);
        if (response == null) {
            log.error("Error Occurred while saving OrderOutboxMessage for Saga {}", orderOutboxMessage.getSagaId().toString());
            throw new PaymentDomainException("Error Occurred while saving OrderOutboxMessage for Saga " + orderOutboxMessage.getSagaId());
        }
        log.info("OrderOutboxMessage is saved with ID {}", orderOutboxMessage.getId());
    }
}
