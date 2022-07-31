package org.sarav.food.order.service.app.mapper;

import org.sarav.food.order.service.app.dto.create.CreateOrderCommand;
import org.sarav.food.order.service.app.dto.create.CreateOrderResponse;
import org.sarav.food.order.service.app.dto.create.OrderAddress;
import org.sarav.food.order.service.app.dto.create.OrderItemEntity;
import org.sarav.food.order.service.domain.entity.Order;
import org.sarav.food.order.service.domain.entity.OrderItem;
import org.sarav.food.order.service.domain.entity.Product;
import org.sarav.food.order.service.domain.entity.Restaurant;
import org.sarav.food.order.service.domain.valueobjects.DeliveryAddress;
import org.sarav.food.system.domain.valueobjects.CustomerId;
import org.sarav.food.system.domain.valueobjects.Money;
import org.sarav.food.system.domain.valueobjects.ProductId;
import org.sarav.food.system.domain.valueobjects.RestaurantId;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class OrderDataMapper {

    public Restaurant createOrderCommandToRestaurant(CreateOrderCommand createOrderCommand) {

        return Restaurant.newBuilder()
                .id(new RestaurantId(createOrderCommand.getRestaurantId()))
                .productList(createOrderCommand.getOrder().stream()
                                .map(orderItem -> Product.newBuilder()
                                        .id(new ProductId(orderItem.getProductId())).build()
                ).toList()).build();

    }

    public Order createOrderCommandToOrder(CreateOrderCommand createOrderCommand) {

        return Order.builder().customerId(new CustomerId(createOrderCommand.getCustomerId()))
                .restaurantId(new RestaurantId(createOrderCommand.getRestaurantId()))
                .price(new Money(createOrderCommand.getPrice()))
                .items(convertOrderItemEntitiesToOrderItems(createOrderCommand.getOrder()))
                .deliveryAddress(convertOrderAddressToDeliveryAddress(createOrderCommand.getAddress()))
                .build();

    }

    private DeliveryAddress convertOrderAddressToDeliveryAddress(OrderAddress address) {
        return new DeliveryAddress(UUID.randomUUID(), address.getAddressLine1(), address.getAddressLine2(), address.getCity(), address.getPostalCode());
    }

    private List<OrderItem> convertOrderItemEntitiesToOrderItems(List<OrderItemEntity> orderItemEntities){

        return orderItemEntities.stream().map(orderItemEntity ->
             OrderItem.newBuilder()
                    .product( Product.newBuilder().id(new ProductId(orderItemEntity.getProductId())).build())
                    .price(new Money(orderItemEntity.getPrice()))
                    .subTotal(new Money(orderItemEntity.getSubTotal()))
                    .quantity(orderItemEntity.getQuantity())
                    .build()).toList();

    }

    public CreateOrderResponse convertOrderToCreateOrderResponse(Order order, String message) {

        return CreateOrderResponse.builder()
                .orderTrackingId(order.getTrackingId().getValue())
                .orderStatus(order.getOrderStatus())
                .response(message).build();

    }
}
