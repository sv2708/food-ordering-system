package org.sarav.food.order.service.domain.valueobjects;

import org.sarav.food.system.domain.valueobjects.BaseId;

import java.util.UUID;

public class TrackingId extends BaseId<UUID> {
    public TrackingId(UUID id) {
        super(id);
    }
}
