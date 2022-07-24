package org.sarav.food.order.service.app.dto.track;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.sarav.food.system.domain.valueobjects.OrderStatus;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Builder
@Getter
public class TrackOrderResponse {
    @NotNull
    private final OrderStatus orderStatus;
    @NotNull
    private final UUID orderTrackingId;
    private final List<String> failureMessages;
}
