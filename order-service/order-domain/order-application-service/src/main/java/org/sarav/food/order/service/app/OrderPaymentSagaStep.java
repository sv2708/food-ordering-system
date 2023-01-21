package org.sarav.food.order.service.app;

import lombok.extern.slf4j.Slf4j;
import org.sarav.food.order.service.app.dto.message.PaymentResponse;
import org.sarav.food.order.service.app.mapper.OrderDataMapper;
import org.sarav.food.order.service.app.outbox.model.approval.OrderApprovalEventPayload;
import org.sarav.food.order.service.app.outbox.model.payment.OrderPaymentOutboxMessage;
import org.sarav.food.order.service.app.outbox.scheduler.approval.RestaurantApprovalOutboxHelper;
import org.sarav.food.order.service.app.outbox.scheduler.payment.PaymentOutboxHelper;
import org.sarav.food.order.service.domain.OrderDomainService;
import org.sarav.food.order.service.domain.entity.Order;
import org.sarav.food.order.service.domain.event.OrderPaidEvent;
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

/***
 *  Responsible for co-ordinating the order payment saga steps
 *  It will be called from the payment response message listener
 *  1) Place Order
 *  2) Get Confirmation of Payment
 *  3) success() will be called if Payment Success. If Payment failed, rollback() will be called to rollback the order.
 */
@Slf4j
@Component
public class OrderPaymentSagaStep implements SagaStep<PaymentResponse> {

    private final OrderSagaHelper orderSagaHelper;
    private final OrderDomainService orderDomainService;
    private final OrderDataMapper orderDataMapper;
    private final PaymentOutboxHelper paymentOutboxHelper;
    private final RestaurantApprovalOutboxHelper restaurantApprovalOutboxHelper;

    public OrderPaymentSagaStep(OrderSagaHelper orderSagaHelper, OrderDomainService orderDomainService,
                                OrderDataMapper orderDataMapper, PaymentOutboxHelper paymentOutboxHelper,
                                RestaurantApprovalOutboxHelper restaurantApprovalOutboxHelper) {
        this.orderSagaHelper = orderSagaHelper;
        this.orderDomainService = orderDomainService;
        this.orderDataMapper = orderDataMapper;
        this.paymentOutboxHelper = paymentOutboxHelper;
        this.restaurantApprovalOutboxHelper = restaurantApprovalOutboxHelper;
    }

    /**
     * This method gets called when the payment response message is received with successful payment
     * Gets called from PaymentResponseMessageListener from Messaging module
     *
     * @param data -> Payment Response data
     */
    @Override
    @Transactional
    public void success(PaymentResponse data) {
        log.info("Completing Payment For order {}", data.getOrderId());
        /* Check for message with saga status as started */
        Optional<OrderPaymentOutboxMessage> outboxMessage =
                paymentOutboxHelper.getPaymentOutboxMessageBySagaIdAndSagaStatus(
                        UUID.fromString(data.getSagaId()),
                        SagaStatus.STARTED
                );
        if (outboxMessage.isEmpty()) {
            /* The outbox message can be read more than once if the saga status is not yet updated.
                In that case, it will not be empty */
            log.info("Order Payment Outbox Message for order {} and Saga Id {} has already been processed",
                    data.getOrderId(), data.getSagaId());
            return;
        }
        Order order = orderSagaHelper.findOrder(data.getOrderId());
        OrderPaidEvent orderPaidEvent = orderDomainService.payOrder(order);
        orderSagaHelper.saveOrder(order);
        log.info("Order Payment Completed for {}. Sending to Restaurant for approval", data.getOrderId());
        paymentOutboxHelper.save(getUpdatedOrderPaymentOutboxMessage(outboxMessage.get(), orderPaidEvent.getOrder().getOrderStatus()));
        OrderApprovalEventPayload orderApprovalEventPayload = orderDataMapper.orderPaidEventToOrderApprovalEventPayload(orderPaidEvent);

        /* After the saga step is complete for payment, start the saga step for Restaurant Approval */
        restaurantApprovalOutboxHelper.saveApprovalOutboxMessage(orderApprovalEventPayload,
                orderPaidEvent.getOrder().getOrderStatus(),
                orderSagaHelper.orderStatusToSagaStatus(orderPaidEvent.getOrder().getOrderStatus()),
                OutboxStatus.STARTED,
                UUID.fromString(data.getSagaId())
        );


    }

    private OrderPaymentOutboxMessage getUpdatedOrderPaymentOutboxMessage(OrderPaymentOutboxMessage outboxMessage,
                                                                          OrderStatus orderStatus) {
        outboxMessage.setProcessedAt(ZonedDateTime.now(ZoneId.of("UTC")));
        outboxMessage.setOrderStatus(orderStatus);
        outboxMessage.setSagaStatus(orderSagaHelper.orderStatusToSagaStatus(orderStatus));
        return outboxMessage;
    }


    @Override
    @Transactional
    public void rollback(PaymentResponse data) {
        log.info("Cancelling the Order {} as the payment is not successful", data.getOrderId());
        Order order = orderSagaHelper.findOrder(data.getOrderId());
        // cancel the order
        order.cancel(data.getFailureMessages());
        orderSagaHelper.saveOrder(order);
        log.info("Cancel of Order {} is Successful", data.getOrderId());
    }

}
