package org.sarav.food.order.service.dataaccess.order.mapper;

import org.sarav.food.order.service.dataaccess.order.entity.OrderAddressEntity;
import org.sarav.food.order.service.dataaccess.order.entity.OrderEntity;
import org.sarav.food.order.service.dataaccess.order.entity.OrderItemEntity;
import org.sarav.food.order.service.domain.entity.Order;
import org.sarav.food.order.service.domain.entity.OrderItem;
import org.sarav.food.order.service.domain.entity.Product;
import org.sarav.food.order.service.domain.valueobjects.DeliveryAddress;
import org.sarav.food.order.service.domain.valueobjects.OrderItemId;
import org.sarav.food.order.service.domain.valueobjects.TrackingId;
import org.sarav.food.order.system.domain.valueobjects.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class OrderEntityDataMapper {

    public OrderEntity OrderToOrderEntity(Order order) {
        var orderEntity = OrderEntity.builder().id(order.getId().getValue())
                .customerId(order.getCustomerId().getValue())
                .restaurantId(order.getRestaurantId().getValue())
                .trackingId(order.getTrackingId().getValue())
                .orderStatus(order.getOrderStatus())
                .address(convertAddressToAddressEntity(order.getDeliveryAddress()))
                .price(order.getPrice().getAmount())
                .failureMessages(String.join(Order.FAILURE_MSG_DELIMITER, order.getFailureMessages()))
                .orderItemEntities(convertOrderItemsToOrderItemEntities(order.getItems()))
                .build();

        orderEntity.getOrderItemEntities().forEach(item -> item.setOrder(orderEntity));
        orderEntity.getAddress().setOrder(orderEntity);
        return orderEntity;
    }

    public Order orderEntityToOrder(OrderEntity orderEntity) {
        return Order.builder().id(new OrderId(orderEntity.getId()))
                .customerId(new CustomerId(orderEntity.getCustomerId()))
                .restaurantId(new RestaurantId(orderEntity.getRestaurantId()))
                .trackingId(new TrackingId(orderEntity.getTrackingId()))
                .items(convertOrderItemToEntities(orderEntity.getOrderItemEntities()))
                .price(new Money(orderEntity.getPrice()))
                .orderStatus(orderEntity.getOrderStatus())
                .deliveryAddress(convertAddressEntityToAddress(orderEntity.getAddress()))
                .failureMessages(orderEntity.getFailureMessages() == null ? new ArrayList<>() :
                        new ArrayList<>(Arrays.asList(orderEntity.getFailureMessages()
                                .split(Order.FAILURE_MSG_DELIMITER))))
                .build();
    }

    private List<OrderItem> convertOrderItemToEntities(List<OrderItemEntity> orderItemEntities) {

        return orderItemEntities.stream()
                .map(item -> OrderItem.newBuilder().id(new OrderItemId(item.getId()))
                        .orderId(new OrderId(item.getOrder().getId()))
                        .product(Product.newBuilder().id(new ProductId(item.getProductId())).build())
                        .quantity(item.getQuantity())
                        .price(new Money(item.getPrice()))
                        .subTotal(new Money(item.getSubTotal()))
                        .build()
                ).collect(Collectors.toList());

    }

    private DeliveryAddress convertAddressEntityToAddress(OrderAddressEntity address) {

        return DeliveryAddress.newBuilder().addressLine1(address.getAddressLine1())
                .addressLine2(address.getAddressLine2()).city(address.getCity())
                .zipcode(address.getZipcode()).build();

    }

    private List<OrderItemEntity> convertOrderItemsToOrderItemEntities(List<OrderItem> items) {
        return items.stream().map(item -> OrderItemEntity.builder()
                .id(item.getId().getValue())
                .productId(item.getProduct().getId().getValue())
                .quantity(item.getQuantity())
                .subTotal(item.getSubTotal().getAmount())
                .price(item.getPrice().getAmount())
                .build()
        ).collect(Collectors.toList());
    }

    private OrderAddressEntity convertAddressToAddressEntity(DeliveryAddress deliveryAddress) {
        return OrderAddressEntity.builder().id(deliveryAddress.getId())
                .addressLine1(deliveryAddress.getAddressLine1())
                .addressLine2(deliveryAddress.getAddressLine2())
                .city(deliveryAddress.getCity())
                .zipcode(deliveryAddress.getZipcode())
                .build();
    }


}
