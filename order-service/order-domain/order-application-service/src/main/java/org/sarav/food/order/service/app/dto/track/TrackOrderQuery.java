package org.sarav.food.order.service.app.dto.track;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Used by client for querying order status
 */

@AllArgsConstructor
@Builder
@Getter
public class TrackOrderQuery {
    @NotNull
    private final UUID trackingOrderId;

}
