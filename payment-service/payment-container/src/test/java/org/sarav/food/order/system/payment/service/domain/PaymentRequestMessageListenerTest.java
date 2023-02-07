package org.sarav.food.order.system.payment.service.domain;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.postgresql.util.PSQLException;
import org.sarav.food.order.system.domain.valueobjects.PaymentOrderStatus;
import org.sarav.food.order.system.domain.valueobjects.PaymentStatus;
import org.sarav.food.order.system.outbox.OutboxStatus;
import org.sarav.food.payment.service.app.dto.PaymentRequest;
import org.sarav.food.payment.service.app.outbox.model.OrderOutboxMessage;
import org.sarav.food.payment.service.app.ports.input.message.listener.PaymentRequestMessageListener;
import org.sarav.food.payment.service.app.ports.output.repository.OrderOutboxRepository;
import org.sarav.food.payment.service.domain.PaymentServiceApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.sarav.food.order.system.saga.order.SagaConstants.ORDER_SAGA_NAME;

@Slf4j
@SpringBootTest(classes = PaymentServiceApplication.class)
public class PaymentRequestMessageListenerTest {

    @Autowired
    private PaymentRequestMessageListener paymentRequestMessageListener;
    @Autowired
    private OrderOutboxRepository orderOutboxRepository;
    private static final String CUSTOMER_ID = "d215b5f8-0249-4dc5-89a3-51fd148cfb41";
    private static final BigDecimal amount = new BigDecimal("100");


    @Test
    public void testDoublePayment() {
        String sagaId = UUID.randomUUID().toString();
        paymentRequestMessageListener.completePayment(getPaymentRequest(sagaId));
        try {
            paymentRequestMessageListener.completePayment(getPaymentRequest(sagaId));
        } catch (DataAccessException e) {
            String sqlState = ((PSQLException) (Objects.requireNonNull(e.getRootCause()))).getSQLState();
            log.error("Exception occurred when tried processing the payment for 2nd time. SQLState: {}. Error: {}", sqlState, e.getMessage());
        }
        assertOrderOutbox(sagaId);
    }

    @Test
    public void testDoublePaymentWithThreads() {
        String sagaId = UUID.randomUUID().toString();
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        try {
            executorService.invokeAll(
                    List.of(Executors.callable(() -> {
                        paymentRequestMessageListener.completePayment(getPaymentRequest(sagaId));
                        assertOrderOutbox(sagaId);
                    }), Executors.callable(() -> {
                                try {
                                    paymentRequestMessageListener.completePayment(getPaymentRequest(sagaId));
                                } catch (DataAccessException e) {
                                    log.error("Data Access Exception Occurred: {}", e.getMessage());
                                }
                            }
                    )));
        } catch (InterruptedException e) {
            log.error("Error Occurred: {}", e.getMessage());
        } finally {
            executorService.shutdown();
        }

    }

    private void assertOrderOutbox(String sagaId) {
        Optional<OrderOutboxMessage> orderOutboxMessage =
                orderOutboxRepository.findByTypeAndSagaIdAndPaymentStatusAndOutboxStatus(
                        ORDER_SAGA_NAME, UUID.fromString(sagaId), PaymentStatus.COMPLETED, OutboxStatus.STARTED
                );

        assertTrue(orderOutboxMessage.isPresent());
        assertEquals(sagaId, orderOutboxMessage.get().getSagaId().toString());

    }

    private PaymentRequest getPaymentRequest(String sagaId) {
        return PaymentRequest.builder()
                .id(UUID.randomUUID().toString())
                .OrderId(UUID.randomUUID().toString())
                .customerId(CUSTOMER_ID)
                .sagaId(sagaId)
                .amount(amount)
                .paymentOrderStatus(PaymentOrderStatus.PENDING)
                .createdAt(Instant.now())
                .build();
    }

}
