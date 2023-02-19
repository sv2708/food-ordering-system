package org.sarav.food.order.service.app.dto.create;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class CreateOrderCommand {

    @NotNull
    private UUID customerId;
    @NotNull
    private UUID restaurantId;
    @NotNull
    private BigDecimal price;
    @NotNull(message = "Order in CreateOrderCommand must not be null")
    private List<OrderItemEntity> items;
    @NotNull
    private OrderAddress address;

    public CreateOrderCommand() {
    }

    public CreateOrderCommand(UUID customerId, UUID restaurantId, BigDecimal price, List<OrderItemEntity> items, OrderAddress address) {
        this.customerId = customerId;
        this.restaurantId = restaurantId;
        this.price = price;
        this.items = items;
        this.address = address;
    }

    private CreateOrderCommand(Builder builder) {
        customerId = builder.customerId;
        restaurantId = builder.restaurantId;
        price = builder.price;
        items = builder.items;
        address = builder.address;
    }


    public UUID getCustomerId() {
        return customerId;
    }

    public UUID getRestaurantId() {
        return restaurantId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public List<OrderItemEntity> getItems() {
        return items;
    }

    public OrderAddress getAddress() {
        return address;
    }

    public static Builder builder() {
        return new Builder();
    }


    public static final class Builder {
        private @NotNull UUID customerId;
        private @NotNull UUID restaurantId;
        private @NotNull BigDecimal price;
        private @NotNull(message = "Order in CreateOrderCommand must not be null") List<OrderItemEntity> items;
        private @NotNull OrderAddress address;

        public Builder() {
        }

        public Builder customerId(@NotNull UUID val) {
            customerId = val;
            return this;
        }

        public Builder restaurantId(@NotNull UUID val) {
            restaurantId = val;
            return this;
        }

        public Builder price(@NotNull BigDecimal val) {
            price = val;
            return this;
        }

        public Builder items(@NotNull(message = "Order in CreateOrderCommand must not be null") List<OrderItemEntity> val) {
            items = val;
            return this;
        }

        public Builder address(@NotNull OrderAddress val) {
            address = val;
            return this;
        }

        public CreateOrderCommand build() {
            return new CreateOrderCommand(this);
        }
    }
}
