package org.sarav.food.order.service.app;

import lombok.extern.slf4j.Slf4j;
import org.sarav.food.order.service.app.dto.message.RestaurantApprovalResponse;
import org.sarav.food.order.service.app.mapper.OrderDataMapper;
import org.sarav.food.order.service.app.outbox.model.approval.OrderApprovalOutboxMessage;
import org.sarav.food.order.service.app.outbox.model.payment.OrderPaymentOutboxMessage;
import org.sarav.food.order.service.app.outbox.scheduler.approval.RestaurantApprovalOutboxHelper;
import org.sarav.food.order.service.app.outbox.scheduler.payment.PaymentOutboxHelper;
import org.sarav.food.order.service.domain.OrderDomainService;
import org.sarav.food.order.service.domain.entity.Order;
import org.sarav.food.order.service.domain.event.OrderCancelledEvent;
import org.sarav.food.order.service.domain.exception.OrderDomainException;
import org.sarav.food.order.system.domain.valueobjects.OrderStatus;
import org.sarav.food.order.system.outbox.OutboxStatus;
import org.sarav.food.order.system.saga.SagaStatus;
import org.sarav.food.order.system.saga.SagaStep;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class OrderApprovalSagaStep implements SagaStep<RestaurantApprovalResponse> {

    private final OrderSagaHelper orderSagaHelper;
    private final OrderDomainService orderDomainService;
    private final PaymentOutboxHelper paymentOutboxHelper;
    private final OrderDataMapper orderDataMapper;
    private final RestaurantApprovalOutboxHelper restaurantApprovalOutboxHelper;

    public OrderApprovalSagaStep(OrderSagaHelper orderSagaHelper,
                                 OrderDomainService orderDomainService, PaymentOutboxHelper paymentOutboxHelper,
                                 OrderDataMapper orderDataMapper, RestaurantApprovalOutboxHelper restaurantApprovalOutboxHelper) {
        this.orderSagaHelper = orderSagaHelper;
        this.orderDomainService = orderDomainService;
        this.paymentOutboxHelper = paymentOutboxHelper;
        this.orderDataMapper = orderDataMapper;
        this.restaurantApprovalOutboxHelper = restaurantApprovalOutboxHelper;
    }

    @Transactional
    @Override
    public void success(RestaurantApprovalResponse restaurantApprovalResponse) {
        log.info("Approving Order {}", restaurantApprovalResponse.getOrderId());

        Optional<OrderApprovalOutboxMessage> orderApprovalOutboxMessageOptional =
                restaurantApprovalOutboxHelper
                        .getApprovalOutboxMessageBySagaIdAndSagaStatus(
                                UUID.fromString(restaurantApprovalResponse.getSagaId()), SagaStatus.PROCESSING);

        if (orderApprovalOutboxMessageOptional.isEmpty()) {
            log.info("Approval Outbox message for Order {} has already been Approved}", restaurantApprovalResponse.getOrderId());
            return;
        }

        OrderApprovalOutboxMessage orderApprovalOutboxMessage = orderApprovalOutboxMessageOptional.get();
        Order order = approveOrder(restaurantApprovalResponse);
        SagaStatus sagaStatus = orderSagaHelper.orderStatusToSagaStatus(order.getOrderStatus());
        restaurantApprovalOutboxHelper.save(getUpdatedApprovalOutboxMessage(orderApprovalOutboxMessage, order.getOrderStatus(), sagaStatus));

        Optional<OrderPaymentOutboxMessage> orderPaymentOutboxMessageOptional =
                paymentOutboxHelper.getPaymentOutboxMessageBySagaIdAndSagaStatus(
                        UUID.fromString(restaurantApprovalResponse.getSagaId()), SagaStatus.PROCESSING);

        if (orderPaymentOutboxMessageOptional.isEmpty()) {
            throw new OrderDomainException("No Order Payment Outbox Message found with Saga Status " + SagaStatus.PROCESSING.name() + ". System is in invalid state");
        }

        OrderPaymentOutboxMessage orderPaymentOutboxMessage = orderPaymentOutboxMessageOptional.get();


        paymentOutboxHelper.save(getUpdatedOrderPaymentOutboxMessage(orderPaymentOutboxMessage,
                order.getOrderStatus(), sagaStatus));

        log.info("Approved the Order {} ", restaurantApprovalResponse.getOrderId());
    }

    private Order approveOrder(RestaurantApprovalResponse restaurantApprovalResponse) {
        Order order = orderSagaHelper.findOrder(restaurantApprovalResponse.getOrderId());
        orderDomainService.approveOrder(order);
        orderSagaHelper.saveOrder(order);
        return order;
    }

    @Transactional
    @Override
    public void rollback(RestaurantApprovalResponse restaurantApprovalResponse) {
        log.info("Cancelling the Order {}", restaurantApprovalResponse.getOrderId());

        Optional<OrderApprovalOutboxMessage> orderApprovalOutboxMessageOptional =
                restaurantApprovalOutboxHelper
                        .getApprovalOutboxMessageBySagaIdAndSagaStatus(
                                UUID.fromString(restaurantApprovalResponse.getSagaId()), SagaStatus.PROCESSING);

        if (orderApprovalOutboxMessageOptional.isEmpty()) {
            log.info("Approval Outbox message for Order {} has already been Rolled back}", restaurantApprovalResponse.getOrderId());
            return;
        }

        OrderCancelledEvent orderCancelledEvent = rollbackOrder(restaurantApprovalResponse);
        OrderApprovalOutboxMessage orderApprovalOutboxMessage = orderApprovalOutboxMessageOptional.get();
        // OrderStatus=CANCELLING; SagaStatus=COMPENSATING
        SagaStatus sagaStatus = orderSagaHelper.orderStatusToSagaStatus(orderCancelledEvent.getOrder().getOrderStatus());
        restaurantApprovalOutboxHelper.save(
                getUpdatedApprovalOutboxMessage(orderApprovalOutboxMessage, orderCancelledEvent.getOrder().getOrderStatus(), sagaStatus)
        );

        /**
         *  After Order service Receives the OrderRejectedEvent from Restaurant Service,
         *  now it needs to send OrderCancelledEvent(Refund) to payment service.
         *  This will be a new Outbox Message.
         */
        paymentOutboxHelper.savePaymentOutboxMessage(
                orderDataMapper.orderCancelledEventToOrderPaymentEventPayload(orderCancelledEvent),
                UUID.fromString(restaurantApprovalResponse.getSagaId()),
                orderCancelledEvent.getOrder().getOrderStatus(), sagaStatus, OutboxStatus.STARTED);
        
        log.info("Cancelling the Order {}", restaurantApprovalResponse.getOrderId());
    }

    private OrderCancelledEvent rollbackOrder(RestaurantApprovalResponse restaurantApprovalResponse) {
        Order order = orderSagaHelper.findOrder(restaurantApprovalResponse.getOrderId());
        //compensating txn to cancel the order and trigger payment refund
        OrderCancelledEvent orderCancelledEvent = orderDomainService.cancelRejectedOrder(order, restaurantApprovalResponse.getFailureMessages());
        orderSagaHelper.saveOrder(order);
        return orderCancelledEvent;
    }


    private OrderPaymentOutboxMessage getUpdatedOrderPaymentOutboxMessage(OrderPaymentOutboxMessage outboxMessage,
                                                                          OrderStatus orderStatus, SagaStatus sagaStatus) {
        outboxMessage.setProcessedAt(ZonedDateTime.now(ZoneId.of("UTC")));
        outboxMessage.setOrderStatus(orderStatus);
        outboxMessage.setSagaStatus(sagaStatus);
        return outboxMessage;
    }

    private OrderApprovalOutboxMessage getUpdatedApprovalOutboxMessage(
            OrderApprovalOutboxMessage orderApprovalOutboxMessage, OrderStatus orderStatus, SagaStatus sagaStatus) {
        log.info("Updating Approval Outbox Message with Saga Id {} to OrderStatus {} and SagaStatus {}",
                orderApprovalOutboxMessage.getSagaId(), orderStatus.name(), sagaStatus.name());
        orderApprovalOutboxMessage.setOrderStatus(orderStatus);
        orderApprovalOutboxMessage.setSagaStatus(sagaStatus);
        orderApprovalOutboxMessage.setProcessedAt(ZonedDateTime.now(ZoneId.of("UTC")));
        return orderApprovalOutboxMessage;
    }

}
