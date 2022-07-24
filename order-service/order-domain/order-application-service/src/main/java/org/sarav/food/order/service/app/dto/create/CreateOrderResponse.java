package org.sarav.food.order.service.app.dto.create;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.sarav.food.system.domain.valueobjects.OrderStatus;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@AllArgsConstructor
@Builder
@Getter
public class CreateOrderResponse {

    @NotNull
    private final UUID orderTrackingId;
    @NotNull
    private final OrderStatus orderStatus;
    @NotNull
    private final String response;

}