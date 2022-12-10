package org.sarav.food.payment.service.domain.valueobjects;

import org.sarav.food.order.system.domain.valueobjects.BaseId;

import java.util.UUID;

public class CreditEntryId extends BaseId<UUID> {

    public CreditEntryId(UUID id) {
        super(id);
    }


}
