package org.sarav.food.order.service.app.mapper;

import org.sarav.food.order.service.app.dto.create.CreateOrderCommand;
import org.sarav.food.order.service.app.dto.create.CreateOrderResponse;
import org.sarav.food.order.service.app.dto.create.OrderAddress;
import org.sarav.food.order.service.app.dto.create.OrderItemEntity;
import org.sarav.food.order.service.app.dto.message.CustomerModel;
import org.sarav.food.order.service.app.dto.track.TrackOrderResponse;
import org.sarav.food.order.service.app.outbox.model.approval.OrderApprovalEventPayload;
import org.sarav.food.order.service.app.outbox.model.approval.OrderApprovalEventProduct;
import org.sarav.food.order.service.app.outbox.model.payment.OrderPaymentEventPayload;
import org.sarav.food.order.service.domain.entity.*;
import org.sarav.food.order.service.domain.event.OrderCancelledEvent;
import org.sarav.food.order.service.domain.event.OrderCreatedEvent;
import org.sarav.food.order.service.domain.event.OrderPaidEvent;
import org.sarav.food.order.service.domain.valueobjects.DeliveryAddress;
import org.sarav.food.order.system.domain.valueobjects.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

    public OrderPaymentEventPayload orderCreatedEventToOrderPaymentEventPayload(OrderCreatedEvent orderCreatedEvent) {

        return OrderPaymentEventPayload.builder()
                .createdAt(orderCreatedEvent.getCreatedAt())
                .orderId(orderCreatedEvent.getOrder().getId().getValue().toString())
                .customerId(orderCreatedEvent.getOrder().getCustomerId().getValue().toString())
                .amount(orderCreatedEvent.getOrder().getPrice().getAmount())
                .paymentOrderStatus(PaymentOrderStatus.PENDING.name())
                .build();

    }

    public OrderApprovalEventPayload orderPaidEventToOrderApprovalEventPayload(OrderPaidEvent orderPaidEvent) {

        return OrderApprovalEventPayload.builder()
                .createdAt(orderPaidEvent.getCreatedAt())
                .orderId(orderPaidEvent.getOrder().getId().getValue().toString())
                .restaurantId(orderPaidEvent.getOrder().getRestaurantId().getValue().toString())
                .restaurantApprovalStatus(RestaurantOrderStatus.PAID.name())
                .products(orderPaidEvent.getOrder().getItems().stream()
                        .map(orderItem -> OrderApprovalEventProduct.builder()
                                .id(orderItem.getProduct().getId().getValue().toString())
                                .quantity(orderItem.getQuantity())
                                .build()).collect(Collectors.toList()))
                .price(orderPaidEvent.getOrder().getPrice().getAmount())
                .build();

    }

    public OrderPaymentEventPayload orderCancelledEventToOrderPaymentEventPayload(OrderCancelledEvent orderCancelledEvent) {
        return OrderPaymentEventPayload.builder()
                .orderId(orderCancelledEvent.getOrder().getId().getValue().toString())
                .createdAt(orderCancelledEvent.getCreatedAt())
                .customerId(orderCancelledEvent.getOrder().getCustomerId().getValue().toString())
                .paymentOrderStatus(PaymentOrderStatus.CANCELLED.name()) // Payment Status should be cancelled
                .amount(orderCancelledEvent.getOrder().getPrice().getAmount())
                .build();
    }

    public Customer customerModelToCustomer(CustomerModel customerModel) {
        return new Customer(new CustomerId(UUID.fromString(customerModel.getId())), customerModel.getUsername(),
                customerModel.getFirstName(), customerModel.getLastName());
    }

}
