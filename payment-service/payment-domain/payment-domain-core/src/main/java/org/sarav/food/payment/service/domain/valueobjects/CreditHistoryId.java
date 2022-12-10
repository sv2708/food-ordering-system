package org.sarav.food.payment.service.domain.valueobjects;

import org.sarav.food.order.system.domain.valueobjects.BaseId;

import java.util.UUID;

public class CreditHistoryId extends BaseId<UUID> {
    public CreditHistoryId(UUID id) {
        super(id);
    }
}
