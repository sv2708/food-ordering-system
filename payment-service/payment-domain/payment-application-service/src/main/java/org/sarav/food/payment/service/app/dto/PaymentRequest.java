package org.sarav.food.payment.service.app.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.sarav.food.payment.service.domain.valueobjects.PaymentOrderStatus;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Builder
@AllArgsConstructor
public class PaymentRequest {

    private String id;
    private String OrderId;
    private String customerId;
    private String sagaId;
    private BigDecimal amount;
    private Instant createdAt;
    private PaymentOrderStatus paymentOrderStatus;


}
