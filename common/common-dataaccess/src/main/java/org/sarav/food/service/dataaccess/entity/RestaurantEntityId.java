package org.sarav.food.service.dataaccess.entity;

import lombok.*;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class RestaurantEntityId implements Serializable {

    private UUID restaurantId;
    private UUID productId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RestaurantEntityId)) return false;
        RestaurantEntityId that = (RestaurantEntityId) o;
        return restaurantId.equals(that.restaurantId) && productId.equals(that.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(restaurantId, productId);
    }
}
