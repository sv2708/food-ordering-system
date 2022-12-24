package org.sarav.food.service.dataaccess.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

/**
 * This entity is a combination of Restaurant(One) and products(Many)
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@IdClass(RestaurantEntityId.class)
@Table(name = "order_restaurant_m_view", schema = "restaurant")
@Entity
public class RestaurantEntity {

    @Id
    private UUID restaurantId;
    @Id
    private UUID productId;

    private String restaurantName;
    private Boolean restaurantActive;
    private String productName;
    private BigDecimal productPrice;
    private Boolean productAvailable;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RestaurantEntity)) return false;
        RestaurantEntity that = (RestaurantEntity) o;
        return restaurantId.equals(that.restaurantId) && productId.equals(that.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(restaurantId, productId);
    }
}
