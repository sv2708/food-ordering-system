package org.sarav.food.order.service.app.dto.create;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
public class CreateOrderCommand {

    @NotNull
    private final UUID customerId;
    @NotNull
    private final UUID restaurantId;
    @NotNull
    private final BigDecimal price;
    @NotNull(message = "Order in CreateOrderCommand must not be null")
    private final List<OrderItemEntity> items;
    @NotNull
    private final OrderAddress address;

}
