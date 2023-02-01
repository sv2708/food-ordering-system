package org.sarav.food.order.system.order.service.domain;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.sarav.food.order.service.app.OrderPaymentSagaStep;
import org.sarav.food.order.service.app.dto.message.PaymentResponse;
import org.sarav.food.order.service.dataaccess.outbox.payment.entity.PaymentOutboxEntity;
import org.sarav.food.order.service.dataaccess.outbox.payment.repository.PaymentOutboxJpaRepository;
import org.sarav.food.order.system.domain.valueobjects.PaymentStatus;
import org.sarav.food.order.system.saga.SagaStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.sarav.food.order.system.saga.order.SagaConstants.ORDER_SAGA_NAME;

@Slf4j
@Sql(value = {"classpath:sql/OrderPaymentSagaTestCleanUp.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = {"classpath:sql/OrderPaymentSagaTestSetUp.sql"})
@SpringBootTest(classes = OrderServiceApplication.class)
public class OrderPaymentSagaTest {

    @Autowired
    private OrderPaymentSagaStep orderPaymentSagaStep;
    private final String ORDER_ID = "d215b5f8-0249-4dc5-89a3-51fd148cfb17";
    private final String SAGA_ID = "15a497c1-0f4b-4eff-b9f4-c402c8c07afa";
    private final String CUSTOMER_ID = "d215b5f8-0249-4dc5-89a3-51fd148cfb41";
    private final String PAYMENT_ID = UUID.randomUUID().toString();
    private final BigDecimal price = new BigDecimal("100");
    @Autowired
    private PaymentOutboxJpaRepository paymentOutboxJpaRepository;

    @Test
    public void testDoublePayment() {
        PaymentResponse paymentResponse = getPaymentResponse();

        orderPaymentSagaStep.success(paymentResponse);
        orderPaymentSagaStep.success(paymentResponse);
    }

    @Test
    public void testDoublePaymentWithThreads() throws InterruptedException {
        PaymentResponse paymentResponse = getPaymentResponse();

        Thread t1 = new Thread(() -> orderPaymentSagaStep.success(paymentResponse));
        Thread t2 = new Thread(() -> orderPaymentSagaStep.success(paymentResponse));

        t1.start();
        t2.start();
        t1.join();
        t2.join();
        assertPaymentOutbox();
    }

    @Test
    public void testDoublePaymentWithCountDownLatch() throws InterruptedException {
        PaymentResponse paymentResponse = getPaymentResponse();
        CountDownLatch latch = new CountDownLatch(2);
        Thread t1 = new Thread(() -> {
            try {
                orderPaymentSagaStep.success(paymentResponse);
            } catch (OptimisticLockingFailureException e) {
                log.error("Optimistic Locking Exception Occurred for Thread t1");
            } finally {
                latch.countDown();
            }
        });
        Thread t2 = new Thread(() -> {
            try {
                orderPaymentSagaStep.success(paymentResponse);
            } catch (OptimisticLockingFailureException e) {
                log.error("Optimistic Locking Exception Occurred for Thread t2");
            } finally {
                latch.countDown();
            }
        });
        t1.start();
        t2.start();

        latch.await();
    }

    private void assertPaymentOutbox() {
        Optional<PaymentOutboxEntity> paymentOutboxEntity = paymentOutboxJpaRepository
                .findByTypeAndSagaIdAndSagaStatusIn(ORDER_SAGA_NAME, UUID.fromString(SAGA_ID),
                        List.of(SagaStatus.PROCESSING));
        assertTrue(paymentOutboxEntity.isPresent());
    }

    private PaymentResponse getPaymentResponse() {
        return PaymentResponse.builder()
                .id(UUID.randomUUID().toString())
                .orderId(ORDER_ID)
                .sagaId(SAGA_ID)
                .customerId(CUSTOMER_ID)
                .paymentId(PAYMENT_ID)
                .paymentStatus(PaymentStatus.COMPLETED)
                .createdAt(Instant.now())
                .failureMessages(new ArrayList<>())
                .price(price)
                .build();
    }
}
