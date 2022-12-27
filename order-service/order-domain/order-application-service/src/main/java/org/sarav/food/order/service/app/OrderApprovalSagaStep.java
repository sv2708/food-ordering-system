package org.sarav.food.order.service.app;

import lombok.extern.slf4j.Slf4j;
import org.sarav.food.order.service.app.dto.message.RestaurantApprovalResponse;
import org.sarav.food.order.service.app.ports.output.message.publisher.payment.OrderCancelledPaymentRequestMessagePublisher;
import org.sarav.food.order.service.domain.OrderDomainService;
import org.sarav.food.order.service.domain.entity.Order;
import org.sarav.food.order.service.domain.event.OrderCancelledEvent;
import org.sarav.food.order.system.domain.event.EmptyEvent;
import org.sarav.food.order.system.saga.SagaStep;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class OrderApprovalSagaStep implements SagaStep<RestaurantApprovalResponse, EmptyEvent, OrderCancelledEvent> {

    private final OrderSagaHelper orderSagaHelper;
    private final OrderCancelledPaymentRequestMessagePublisher orderCancelledPaymentRequestMessagePublisher;
    private final OrderDomainService orderDomainService;

    public OrderApprovalSagaStep(OrderSagaHelper orderSagaHelper,
                                 OrderCancelledPaymentRequestMessagePublisher orderCancelledPaymentRequestMessagePublisher,
                                 OrderDomainService orderDomainService) {
        this.orderSagaHelper = orderSagaHelper;
        this.orderCancelledPaymentRequestMessagePublisher = orderCancelledPaymentRequestMessagePublisher;
        this.orderDomainService = orderDomainService;
    }

    @Transactional
    @Override
    public EmptyEvent success(RestaurantApprovalResponse data) {
        log.info("Approving Order {}", data.getOrderId());
        Order order = orderSagaHelper.findOrder(data.getOrderId());
        orderDomainService.approveOrder(order);
        orderSagaHelper.saveOrder(order);
        log.info("Approved the Order {} ", data.getOrderId());
        return EmptyEvent.getInstance();
    }

    @Transactional
    @Override
    public OrderCancelledEvent rollback(RestaurantApprovalResponse data) {
        log.info("Cancelling the Order {}", data.getOrderId());
        Order order = orderSagaHelper.findOrder(data.getOrderId());
        //compensating txn to cancel the order and trigger payment refund
        OrderCancelledEvent orderCancelledEvent = orderDomainService.cancelOrderPayment(order, data.getFailureMessages(), orderCancelledPaymentRequestMessagePublisher);
        orderSagaHelper.saveOrder(order);
        log.info("Cancelled the Order {}", data.getOrderId());
        return orderCancelledEvent;
    }
}
