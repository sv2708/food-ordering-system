package org.sarav.food.system.domain.valueobjects;

import java.util.UUID;

public class ProductId extends BaseId<UUID> {

    protected ProductId(UUID id) {
        super(id);
    }
}
