package org.sarav.food.order.service.app.dto.create;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@Builder
@Getter
public class OrderAddress {

    @NotNull
    @Max(value=250)
    private final String street;
    @Max(value=20)
    @NotNull
    private final String postalCode;
    @Max(value=50)
    @NotNull
    private final String city;

}
