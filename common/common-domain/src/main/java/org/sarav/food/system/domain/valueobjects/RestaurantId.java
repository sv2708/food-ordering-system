package org.sarav.food.system.domain.valueobjects;

import java.util.UUID;

public class RestaurantId extends BaseId<UUID> {
    protected RestaurantId(UUID id) {
        super(id);
    }
}
