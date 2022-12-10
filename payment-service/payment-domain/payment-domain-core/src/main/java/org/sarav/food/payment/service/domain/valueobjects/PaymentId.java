package org.sarav.food.payment.service.domain.valueobjects;

import org.sarav.food.order.system.domain.valueobjects.BaseId;

import java.util.UUID;

public class PaymentId extends BaseId<UUID> {
    public PaymentId(UUID id) {
        super(id);
    }
}
