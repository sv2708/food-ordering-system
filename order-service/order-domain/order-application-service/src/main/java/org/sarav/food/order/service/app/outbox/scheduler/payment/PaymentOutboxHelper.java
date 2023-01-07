package org.sarav.food.order.service.app.outbox.scheduler.payment;

import lombok.extern.slf4j.Slf4j;
import org.sarav.food.order.service.app.outbox.model.payment.OrderPaymentOutboxMessage;
import org.sarav.food.order.service.app.ports.output.repository.PaymentOutboxRepository;
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
public class PaymentOutboxHelper {

    private final PaymentOutboxRepository paymentOutboxRepository;

    public PaymentOutboxHelper(PaymentOutboxRepository paymentOutboxRepository) {
        this.paymentOutboxRepository = paymentOutboxRepository;
    }

    @Transactional(readOnly = true)
    public Optional<List<OrderPaymentOutboxMessage>> getPaymentOutboxMessagesByOutboxStatusAndSagaStatus(
            OutboxStatus outboxStatus,
            SagaStatus... sagaStatuses) {
        return paymentOutboxRepository.findByTypeAndOutboxStatusAndSagaStatusIn(ORDER_SAGA_NAME, outboxStatus, sagaStatuses);
    }

    @Transactional(readOnly = true)
    public Optional<OrderPaymentOutboxMessage> getPaymentOutboxMessageBySagaIdAndSagaStatus(String type,
                                                                                            UUID sagaId,
                                                                                            SagaStatus sagaStatus) {
        return paymentOutboxRepository.findByTypeAndSagaIdAndSagaStatus(ORDER_SAGA_NAME, sagaId, sagaStatus);
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
}
