package org.sarav.food.order.service.app.mapper;

import org.sarav.food.order.service.app.dto.create.CreateOrderCommand;
import org.sarav.food.order.service.app.dto.create.CreateOrderResponse;
import org.sarav.food.order.service.app.dto.create.OrderAddress;
import org.sarav.food.order.service.app.dto.create.OrderItemEntity;
import org.sarav.food.order.service.app.dto.track.TrackOrderResponse;
import org.sarav.food.order.service.domain.entity.Order;
import org.sarav.food.order.service.domain.entity.OrderItem;
import org.sarav.food.order.service.domain.entity.Product;
import org.sarav.food.order.service.domain.entity.Restaurant;
import org.sarav.food.order.service.domain.valueobjects.DeliveryAddress;
import org.sarav.food.order.system.domain.valueobjects.CustomerId;
import org.sarav.food.order.system.domain.valueobjects.Money;
import org.sarav.food.order.system.domain.valueobjects.ProductId;
import org.sarav.food.order.system.domain.valueobjects.RestaurantId;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class OrderDataMapper {

    public Restaurant createOrderCommandToRestaurant(CreateOrderCommand createOrderCommand) {

        return Restaurant.builder()
                .id(new RestaurantId(createOrderCommand.getRestaurantId()))
                .productList(createOrderCommand.getItems().stream()
                        .map(orderItem -> Product.builder()
                                .id(new ProductId(orderItem.getProductId())).build()
                        ).toList()).build();

    }

    public Order createOrderCommandToOrder(CreateOrderCommand createOrderCommand) {

        return Order.builder().customerId(new CustomerId(createOrderCommand.getCustomerId()))
                .restaurantId(new RestaurantId(createOrderCommand.getRestaurantId()))
                .price(new Money(createOrderCommand.getPrice()))
                .items(convertOrderItemEntitiesToOrderItems(createOrderCommand.getItems()))
                .deliveryAddress(convertOrderAddressToDeliveryAddress(createOrderCommand.getAddress()))
                .failureMessages(new ArrayList<>())
                .build();

    }

    private DeliveryAddress convertOrderAddressToDeliveryAddress(OrderAddress address) {
        return new DeliveryAddress(UUID.randomUUID(), address.getAddressLine1(), address.getAddressLine2(), address.getPostalCode(), address.getCity());
    }

    private List<OrderItem> convertOrderItemEntitiesToOrderItems(List<OrderItemEntity> orderItemEntities) {

        return orderItemEntities.stream().map(orderItemEntity ->
                OrderItem.newBuilder()
                        .product(Product.builder()
                                .id(new ProductId(orderItemEntity.getProductId()))
                                .price(new Money(orderItemEntity.getPrice()))
                                .build())
                        .price(new Money(orderItemEntity.getPrice()))
                        .subTotal(new Money(orderItemEntity.getSubTotal()))
                        .quantity(orderItemEntity.getQuantity())
                        .build()).toList();

    }

    public CreateOrderResponse convertOrderToCreateOrderResponse(Order order) {

        return CreateOrderResponse.builder()
                .orderTrackingId(order.getTrackingId().getValue())
                .orderStatus(order.getOrderStatus())
                .build();

    }

    public TrackOrderResponse convertOrderToTrackOrderResponse(Order order) {

        return TrackOrderResponse.builder()
                .orderTrackingId(order.getTrackingId().getValue())
                .orderStatus(order.getOrderStatus())
                .failureMessages(order.getFailureMessages())
                .build();
    }

}
