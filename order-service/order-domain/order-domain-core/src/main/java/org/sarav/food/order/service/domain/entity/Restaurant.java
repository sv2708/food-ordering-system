package org.sarav.food.order.service.domain.entity;

import org.sarav.food.order.system.domain.entity.AggregateRoot;
import org.sarav.food.order.system.domain.valueobjects.RestaurantId;

import java.util.List;
import java.util.Objects;

public class Restaurant extends AggregateRoot<RestaurantId> {

    private boolean active;
    private final List<Product> productList;

    private Restaurant(Builder builder) {
        super.setId(builder.id);
        active = builder.active;
        productList = builder.productList;
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean isActive() {
        return active;
    }

    public List<Product> getProductList() {
        return productList;
    }

    public static final class Builder {
        private RestaurantId id;
        private boolean active;
        private List<Product> productList;

        private Builder() {
        }

        public Builder id(RestaurantId val) {
            id = val;
            return this;
        }

        public Builder active(boolean val) {
            active = val;
            return this;
        }

        public Builder productList(List<Product> val) {
            productList = val;
            return this;
        }

        public Restaurant build() {
            return new Restaurant(this);
        }


    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Restaurant)) return false;
        if (!super.equals(o)) return false;
        Restaurant that = (Restaurant) o;
        return active == that.active && productList.equals(that.productList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), active, productList);
    }
}
