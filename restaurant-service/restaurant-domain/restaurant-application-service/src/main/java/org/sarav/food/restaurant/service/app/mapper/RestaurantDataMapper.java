package org.sarav.food.restaurant.service.app.mapper;

import org.sarav.food.order.system.domain.valueobjects.Money;
import org.sarav.food.order.system.domain.valueobjects.OrderId;
import org.sarav.food.order.system.domain.valueobjects.OrderStatus;
import org.sarav.food.order.system.domain.valueobjects.RestaurantId;
import org.sarav.food.restaurant.service.app.dto.RestaurantApprovalRequest;
import org.sarav.food.restaurant.service.domain.entity.OrderDetail;
import org.sarav.food.restaurant.service.domain.entity.Product;
import org.sarav.food.restaurant.service.domain.entity.Restaurant;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class RestaurantDataMapper {
    public Restaurant restaurantApprovalRequestToRestaurant(RestaurantApprovalRequest
                                                                    restaurantApprovalRequest) {
        return Restaurant.builder()
                .restaurantId(new RestaurantId(UUID.fromString(restaurantApprovalRequest.getRestaurantId())))
                .orderDetail(OrderDetail.builder()
                        .orderId(new OrderId(UUID.fromString(restaurantApprovalRequest.getOrderId())))
                        .products(restaurantApprovalRequest.getProducts().stream().map(
                                        product -> Product.builder()
                                                .productId(product.getId())
                                                .quantity(product.getQuantity())
                                                .build())
                                .collect(Collectors.toList()))
                        .totalAmount(new Money(restaurantApprovalRequest.getPrice()))
                        .orderStatus(OrderStatus.valueOf(restaurantApprovalRequest.getRestaurantOrderStatus().name()))
                        .build())
                .build();
    }
}
