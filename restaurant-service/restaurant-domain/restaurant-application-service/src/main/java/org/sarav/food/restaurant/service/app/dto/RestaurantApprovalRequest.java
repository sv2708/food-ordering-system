package org.sarav.food.restaurant.service.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.sarav.food.order.system.domain.valueobjects.RestaurantOrderStatus;
import org.sarav.food.restaurant.service.domain.entity.Product;

@Getter
@Builder
@AllArgsConstructor
public class RestaurantApprovalRequest {
    private String id;
    private String sagaId;
    private String restaurantId;
    private String orderId;
    private RestaurantOrderStatus restaurantOrderStatus;
    private java.util.List<Product> products;
    private java.math.BigDecimal price;
    private java.time.Instant createdAt;
}
