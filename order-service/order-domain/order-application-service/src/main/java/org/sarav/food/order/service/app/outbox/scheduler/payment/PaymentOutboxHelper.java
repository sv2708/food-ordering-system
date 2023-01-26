package org.sarav.food.order.service.app.outbox.scheduler.payment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.sarav.food.order.service.app.outbox.model.payment.OrderPaymentEventPayload;
import org.sarav.food.order.service.app.outbox.model.payment.OrderPaymentOutboxMessage;
import org.sarav.food.order.service.app.ports.output.repository.PaymentOutboxRepository;
import org.sarav.food.order.service.domain.exception.OrderDomainException;
import org.sarav.food.order.system.domain.valueobjects.OrderStatus;
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
public class PaymentOutboxHelper {

    private final PaymentOutboxRepository paymentOutboxRepository;
    private final ObjectMapper objectMapper;

    public PaymentOutboxHelper(PaymentOutboxRepository paymentOutboxRepository, ObjectMapper objectMapper) {
        this.paymentOutboxRepository = paymentOutboxRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional(readOnly = true)
    public Optional<List<OrderPaymentOutboxMessage>> getPaymentOutboxMessagesByOutboxStatusAndSagaStatus(
            OutboxStatus outboxStatus,
            SagaStatus... sagaStatuses) {
        return paymentOutboxRepository.findByTypeAndOutboxStatusAndSagaStatusIn(ORDER_SAGA_NAME, outboxStatus, sagaStatuses);
    }

    @Transactional(readOnly = true)
    public Optional<OrderPaymentOutboxMessage> getPaymentOutboxMessageBySagaIdAndSagaStatus(
            UUID sagaId,
            SagaStatus... sagaStatus) {
        return paymentOutboxRepository.findByTypeAndSagaIdAndSagaStatusIn(ORDER_SAGA_NAME, sagaId, sagaStatus);
    }

    @Transactional
    public void save(OrderPaymentOutboxMessage orderPaymentOutboxMessage) {
        OrderPaymentOutboxMessage response = paymentOutboxRepository.save(orderPaymentOutboxMessage);
        if (response == null) {
            log.error("Could not save the updated OrderPaymentOutboxMessage {}", orderPaymentOutboxMessage.getId());
            throw new OrderDomainException("Could not save the updated OrderPaymentOutboxMessage " + orderPaymentOutboxMessage.getId());
        }
        log.info("OrderPaymentOutboxMessage with Id {} has been successfully saved", orderPaymentOutboxMessage.getId());
    }

    @Transactional
    public void deletePaymentOutboxMessageByOutboxStatusAndSagaStatus(OutboxStatus outboxStatus,
                                                                      SagaStatus... sagaStatuses) {
        paymentOutboxRepository.deleteByTypeAndOutboxStatusAndSagaStatus(ORDER_SAGA_NAME, outboxStatus, sagaStatuses);
    }

    public void savePaymentOutboxMessage(OrderPaymentEventPayload orderPaymentEventPayload, UUID sagaId,
                                         OrderStatus orderStatus, SagaStatus sagaStatus, OutboxStatus outboxStatus) {
        save(
                OrderPaymentOutboxMessage.builder()
                        .id(UUID.randomUUID())
                        .createdAt(orderPaymentEventPayload.getCreatedAt())
                        .sagaId(sagaId)
                        .type(ORDER_SAGA_NAME)
                        .orderStatus(orderStatus)
                        .sagaStatus(sagaStatus)
                        .outboxStatus(outboxStatus)
                        .payload(createPayload(orderPaymentEventPayload))
                        .build()
        );
    }

    private String createPayload(OrderPaymentEventPayload orderPaymentEventPayload) {

        try {
            return objectMapper.writeValueAsString(orderPaymentEventPayload);
        } catch (JsonProcessingException e) {
            log.error("Error Occurred when processing JSON {}", e.getMessage());
            throw new OrderDomainException("Error in OrderPaymentEventPayload"
                    + orderPaymentEventPayload.getOrderId()
                    + e.getMessage());
        }

    }

}
