package org.sarav.food.order.service.app;

import lombok.extern.slf4j.Slf4j;
import org.sarav.food.order.service.app.dto.message.PaymentResponse;
import org.sarav.food.order.service.app.ports.output.message.publisher.restaurantapproval.OrderPaidRestaurantRequestMessagePublisher;
import org.sarav.food.order.service.domain.OrderDomainService;
import org.sarav.food.order.service.domain.entity.Order;
import org.sarav.food.order.service.domain.event.OrderPaidEvent;
import org.sarav.food.order.system.domain.event.EmptyEvent;
import org.sarav.food.order.system.saga.SagaStep;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class OrderPaymentSagaStep implements SagaStep<PaymentResponse, OrderPaidEvent, EmptyEvent> {

    private final OrderSagaHelper orderSagaHelper;
    private final OrderDomainService orderDomainService;
    private final OrderPaidRestaurantRequestMessagePublisher orderPaidRestaurantRequestMessagePublisher;

    public OrderPaymentSagaStep(OrderSagaHelper orderSagaHelper, OrderDomainService orderDomainService,
                                OrderPaidRestaurantRequestMessagePublisher orderPaidRestaurantRequestMessagePublisher) {
        this.orderSagaHelper = orderSagaHelper;
        this.orderDomainService = orderDomainService;
        this.orderPaidRestaurantRequestMessagePublisher = orderPaidRestaurantRequestMessagePublisher;
    }

    @Override
    @Transactional
    public OrderPaidEvent success(PaymentResponse data) {
        log.info("Completing Payment For order {}", data.getOrderId());
        Order order = orderSagaHelper.findOrder(data.getOrderId());
        OrderPaidEvent orderPaidEvent = orderDomainService.payOrder(order, orderPaidRestaurantRequestMessagePublisher);
        orderSagaHelper.saveOrder(order);
        log.info("Order Payment Completed for {}. Sending to Restaurant for approval", data.getOrderId());
        return orderPaidEvent;
    }


    @Override
    @Transactional
    public EmptyEvent rollback(PaymentResponse data) {
        log.info("Cancelling the Order {} as the payment is not successful", data.getOrderId());
        Order order = orderSagaHelper.findOrder(data.getOrderId());
        // cancel the order
        order.cancel(data.getFailureMessages());
        orderSagaHelper.saveOrder(order);
        log.info("Cancel of Order {} is Successful", data.getOrderId());
        return EmptyEvent.getInstance();
    }

}
