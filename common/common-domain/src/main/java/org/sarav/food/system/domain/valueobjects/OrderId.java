package org.sarav.food.system.domain.valueobjects;

import java.util.UUID;

public class OrderId extends BaseId<UUID> {

    public OrderId(UUID id) {
        super(id);
    }

}
