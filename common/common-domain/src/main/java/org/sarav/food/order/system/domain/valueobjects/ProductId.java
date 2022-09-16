package org.sarav.food.order.system.domain.valueobjects;

import java.util.UUID;

public class ProductId extends BaseId<UUID> {

    public ProductId(UUID id) {
        super(id);
    }
}
