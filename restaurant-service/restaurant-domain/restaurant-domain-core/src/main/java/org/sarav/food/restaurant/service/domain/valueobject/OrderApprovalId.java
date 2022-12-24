package org.sarav.food.restaurant.service.domain.valueobject;


import org.sarav.food.order.system.domain.valueobjects.BaseId;

import java.util.UUID;

public class OrderApprovalId extends BaseId<UUID> {
    public OrderApprovalId(UUID value) {
        super(value);
    }
}
