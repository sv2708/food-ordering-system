package org.sarav.food.order.service.app;

import lombok.extern.slf4j.Slf4j;
import org.sarav.food.order.service.app.dto.create.CreateOrderCommand;
import org.sarav.food.order.service.app.dto.create.CreateOrderResponse;
import org.sarav.food.order.service.app.mapper.OrderDataMapper;
import org.sarav.food.order.service.app.outbox.scheduler.payment.PaymentOutboxHelper;
import org.sarav.food.order.service.domain.event.OrderCreatedEvent;
import org.sarav.food.order.system.outbox.OutboxStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@Slf4j
public class OrderCreateCommandHandler {

    private final OrderCreateHelper orderCreateHelper;
    private final OrderDataMapper orderDataMapper;
    private final PaymentOutboxHelper paymentOutboxHelper;
    private final OrderSagaHelper orderSagaHelper;

    public OrderCreateCommandHandler(OrderCreateHelper orderCreateHelper, OrderDataMapper orderDataMapper,
                                     PaymentOutboxHelper paymentOutboxHelper, OrderSagaHelper orderSagaHelper) {
        this.orderCreateHelper = orderCreateHelper;
        this.orderDataMapper = orderDataMapper;
        this.paymentOutboxHelper = paymentOutboxHelper;
        this.orderSagaHelper = orderSagaHelper;
    }

    @Transactional
    public CreateOrderResponse handleCreateOrder(CreateOrderCommand createOrderCommand) {
        OrderCreatedEvent orderCreatedEvent = orderCreateHelper.persistOrder(createOrderCommand);
        paymentOutboxHelper.savePaymentOutboxMessage(
                orderDataMapper.orderCreatedEventToOrderPaymentEventPayload(orderCreatedEvent),
                UUID.randomUUID(), // sagaId
                orderCreatedEvent.getOrder().getOrderStatus(),
                orderSagaHelper.orderStatusToSagaStatus(orderCreatedEvent.getOrder().getOrderStatus()),
                OutboxStatus.STARTED
        );
        return orderDataMapper.convertOrderToCreateOrderResponse(orderCreatedEvent.getOrder());
    }


}
