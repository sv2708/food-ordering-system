package org.sarav.food.payment.service.app.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.sarav.food.order.system.domain.valueobjects.PaymentOrderStatus;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
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
