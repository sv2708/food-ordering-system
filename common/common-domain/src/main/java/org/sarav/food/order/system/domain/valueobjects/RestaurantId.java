package org.sarav.food.order.system.domain.valueobjects;

import java.util.UUID;

public class RestaurantId extends BaseId<UUID> {
    public RestaurantId(UUID id) {
        super(id);
    }
}
