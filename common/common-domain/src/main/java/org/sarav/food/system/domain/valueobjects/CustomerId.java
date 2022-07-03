package org.sarav.food.system.domain.valueobjects;

import java.util.UUID;

public class CustomerId extends BaseId<UUID>{
    protected CustomerId(UUID id) {
        super(id);
    }
}
