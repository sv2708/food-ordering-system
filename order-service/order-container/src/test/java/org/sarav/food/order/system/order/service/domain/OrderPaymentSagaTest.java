package org.sarav.food.order.system.order.service.domain;

import org.junit.jupiter.api.Test;
import org.sarav.food.order.service.app.OrderPaymentSagaStep;
import org.sarav.food.order.service.app.dto.message.PaymentResponse;
import org.sarav.food.order.system.domain.valueobjects.PaymentStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;

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

    @Test
    public void testDoublePayment() {
        PaymentResponse paymentResponse = getPaymentResponse();

        orderPaymentSagaStep.success(paymentResponse);
        orderPaymentSagaStep.success(paymentResponse);
    }

    private PaymentResponse getPaymentResponse() {
        return PaymentResponse.builder()
                .id(UUID.randomUUID().toString())
                .orderId(ORDER_ID)
                .sagaId(SAGA_ID)
                .customerId(CUSTOMER_ID)
//                .paymentId(PAYMENT_ID)
                .paymentStatus(PaymentStatus.COMPLETED)
                .createdAt(Instant.now())
                .failureMessages(new ArrayList<>())
                .price(price)
                .build();
    }
}
