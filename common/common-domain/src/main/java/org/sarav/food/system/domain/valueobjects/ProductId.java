package org.sarav.food.system.domain.valueobjects;

import java.util.UUID;

public class ProductId extends BaseId<UUID> {

    public ProductId(UUID id) {
        super(id);
    }
}
