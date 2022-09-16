package org.sarav.food.order.service.messaging.mapper;

import org.sarav.food.order.OrderPaymentStatus;
import org.sarav.food.order.PaymentRequestAvroModel;
import org.sarav.food.order.service.domain.event.OrderCancelledEvent;
import org.sarav.food.order.service.domain.event.OrderCreatedEvent;

import java.util.UUID;

public class OrderMessagingDataMapper {

    public PaymentRequestAvroModel orderCreatedEventToPaymentRequestAvroModel(OrderCreatedEvent orderCreatedEvent) {

        return PaymentRequestAvroModel.newBuilder()
                .setCreatedAt(orderCreatedEvent.getCreatedAt().toInstant())
                .setOrderId(orderCreatedEvent.getOrder().getId().getValue().toString())
                .setId(UUID.randomUUID().toString())
                .setOrderPaymentStatus(OrderPaymentStatus.PENDING)
                .setSagaId("")
                .setPrice(orderCreatedEvent.getOrder().getPrice().getAmount())
                .build();
    }

    public PaymentRequestAvroModel orderCancelledEventToPaymentRequestAvroModel(OrderCancelledEvent orderCancelledEvent) {

        return PaymentRequestAvroModel.newBuilder()
                .setCreatedAt(orderCancelledEvent.getCreatedAt().toInstant())
                .setOrderId(orderCancelledEvent.getOrder().getId().getValue().toString())
                .setId(UUID.randomUUID().toString())
                .setOrderPaymentStatus(OrderPaymentStatus.ORDER_CANCELLED)
                .setSagaId("")
                .setPrice(orderCancelledEvent.getOrder().getPrice().getAmount())
                .build();
    }


}
