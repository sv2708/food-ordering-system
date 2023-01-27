package org.sarav.food.order.service.app;

import lombok.extern.slf4j.Slf4j;
import org.sarav.food.order.service.app.dto.message.PaymentResponse;
import org.sarav.food.order.service.app.mapper.OrderDataMapper;
import org.sarav.food.order.service.app.outbox.model.approval.OrderApprovalEventPayload;
import org.sarav.food.order.service.app.outbox.model.approval.OrderApprovalOutboxMessage;
import org.sarav.food.order.service.app.outbox.model.payment.OrderPaymentOutboxMessage;
import org.sarav.food.order.service.app.outbox.scheduler.approval.RestaurantApprovalOutboxHelper;
import org.sarav.food.order.service.app.outbox.scheduler.payment.PaymentOutboxHelper;
import org.sarav.food.order.service.domain.OrderDomainService;
import org.sarav.food.order.service.domain.entity.Order;
import org.sarav.food.order.service.domain.event.OrderPaidEvent;
import org.sarav.food.order.service.domain.exception.OrderDomainException;
import org.sarav.food.order.system.domain.valueobjects.OrderStatus;
import org.sarav.food.order.system.domain.valueobjects.PaymentStatus;
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
     * @param paymentResponse -> Payment Response data
     */
    @Override
    @Transactional
    public void success(PaymentResponse paymentResponse) {
        log.info("Completing Payment For order {}", paymentResponse.getOrderId());
        /* Check for message with saga status as started */
        Optional<OrderPaymentOutboxMessage> outboxMessage =
                paymentOutboxHelper.getPaymentOutboxMessageBySagaIdAndSagaStatus(
                        UUID.fromString(paymentResponse.getSagaId()),
                        SagaStatus.STARTED
                );
        if (outboxMessage.isEmpty()) {
            /**

             The Same outbox message can be read more than once if the saga status is not yet updated.
             In that case, this outboxMessage field will not be empty.
             So for that we use Optimistic locking on the PaymentOutboxEntity.

             Consider 2 threads entering this method at the same time. Thread 1 will retreive the message from
             Outbox table with SagaStatus as STARTED. It updates the Saga Status as PROCESSING and save it to DB.
             If the transaction gets committed before thread 2 writes to DB, when thread 2 tries to update same entity
             to the DB, the version property in the entity will not be same as it was already incremented by Thread 1.
             So for Thread 2 OptimisticLockException will be thrown.

             Only either Thread 1 or Thread 2 can update the entity
             as the isolation level of Postgres is Read Committed. So either thread will wait until the other one commits
             and releases the lock acquired.

             */
            log.info("Order Payment Outbox Message for order {} and Saga Id {} has already been processed",
                    paymentResponse.getOrderId(), paymentResponse.getSagaId());
            return;
        }
        OrderPaidEvent orderPaidEvent = completeOrderPayment(paymentResponse);
        log.info("Order Payment Completed for {}. Sending to Restaurant for approval", paymentResponse.getOrderId());
        SagaStatus sagaStatus = orderSagaHelper.orderStatusToSagaStatus(orderPaidEvent.getOrder().getOrderStatus());
        paymentOutboxHelper.save(getUpdatedOrderPaymentOutboxMessage(outboxMessage.get(),
                orderPaidEvent.getOrder().getOrderStatus(), sagaStatus));
        OrderApprovalEventPayload orderApprovalEventPayload = orderDataMapper.orderPaidEventToOrderApprovalEventPayload(orderPaidEvent);

        /* After the saga step is complete for payment, start the saga step for Restaurant Approval */
        restaurantApprovalOutboxHelper.saveApprovalOutboxMessage(orderApprovalEventPayload,
                orderPaidEvent.getOrder().getOrderStatus(),
                sagaStatus, // PROCESSING
                OutboxStatus.STARTED,
                UUID.fromString(paymentResponse.getSagaId())
        );


    }

    /**
     * Modifies the outboxMessage with right SagaStatus and OrderStatus and updated time
     *
     * @param outboxMessage
     * @param orderStatus
     * @return
     */
    private OrderPaymentOutboxMessage getUpdatedOrderPaymentOutboxMessage(OrderPaymentOutboxMessage outboxMessage,
                                                                          OrderStatus orderStatus, SagaStatus sagaStatus) {
        outboxMessage.setProcessedAt(ZonedDateTime.now(ZoneId.of("UTC")));
        outboxMessage.setOrderStatus(orderStatus);
        outboxMessage.setSagaStatus(sagaStatus);
        return outboxMessage;
    }

    private OrderPaidEvent completeOrderPayment(PaymentResponse paymentResponse) {
        log.info("Completing Order payment for Order {}", paymentResponse.getOrderId());
        Order order = orderSagaHelper.findOrder(paymentResponse.getOrderId());
        OrderPaidEvent orderPaidEvent = orderDomainService.payOrder(order);
        orderSagaHelper.saveOrder(order);
        return orderPaidEvent;
    }

    @Override
    @Transactional
    public void rollback(PaymentResponse paymentResponse) {
        log.info("Cancelling the Order {} as the payment is not successful", paymentResponse.getOrderId());
        SagaStatus[] sagaStatuses = getCurrentSagaStatus(paymentResponse.getPaymentStatus());
        Optional<OrderPaymentOutboxMessage> outboxMessage =
                paymentOutboxHelper.getPaymentOutboxMessageBySagaIdAndSagaStatus(
                        UUID.fromString(paymentResponse.getSagaId()),
                        sagaStatuses
                );
        if (outboxMessage.isEmpty()) {
            log.info("Payment Outbox Message for Payment Saga with ID {} has already been processed", paymentResponse.getSagaId());
            return;
        }
        // cancel the order
        Order order = rollbackPaymentForOrder(paymentResponse);
        /**
         * Now the OrderStatus is CANCELLED.
         * So the SagaStatus needs to be updated to COMPENSATED
         */
        SagaStatus sagaStatus = orderSagaHelper.orderStatusToSagaStatus(order.getOrderStatus());
        paymentOutboxHelper.save(
                getUpdatedOrderPaymentOutboxMessage(outboxMessage.get(),
                        order.getOrderStatus(), sagaStatus)); // update payment outbox message

        /**
         * If the rollback is happening because of payment cancelled event(refund) as the order rejected at the restaurant level,
         * then also update the message that is present in the Approval Outbox Table.
         * This will complete the transaction.
         * We don't need this when rolling back for payment failed event(insufficient credit)
         * as there will be no entry yet in the Approval Outbox Table.
         */
        if (paymentResponse.getPaymentStatus() == PaymentStatus.CANCELLED) {
            restaurantApprovalOutboxHelper.save(getUpdatedOrderApprovalOutboxMessage(
                    UUID.fromString(paymentResponse.getSagaId()),
                    order.getOrderStatus(), sagaStatus)); // update order approval outbox message
        }

        log.info("Cancel of Order {} is Successful", order.getId().getValue().toString());
    }

    private OrderApprovalOutboxMessage getUpdatedOrderApprovalOutboxMessage(UUID sagaId,
                                                                            OrderStatus orderStatus,
                                                                            SagaStatus sagaStatus) {
        Optional<OrderApprovalOutboxMessage> orderApprovalOutboxMessageOptional =
                restaurantApprovalOutboxHelper.getApprovalOutboxMessageBySagaIdAndSagaStatus(sagaId,
                        SagaStatus.COMPENSATING);
        if (orderApprovalOutboxMessageOptional.isEmpty()) {
            log.error("Approval Outbox Helper Message for Saga {} is not found with status {}", sagaId, SagaStatus.COMPENSATING.name());
            throw new OrderDomainException("Approval Outbox Helper Message for Saga " + sagaId + "with status " + SagaStatus.COMPENSATING.name());
        }
        OrderApprovalOutboxMessage orderApprovalOutboxMessage = orderApprovalOutboxMessageOptional.get();
        orderApprovalOutboxMessage.setOrderStatus(orderStatus);
        orderApprovalOutboxMessage.setProcessedAt(ZonedDateTime.now(ZoneId.of("UTC")));
        orderApprovalOutboxMessage.setSagaStatus(sagaStatus);
        return orderApprovalOutboxMessage;
    }

    private Order rollbackPaymentForOrder(PaymentResponse paymentResponse) {
        log.info("Cancelling Order with Id {}", paymentResponse.getOrderId());
        Order order = orderSagaHelper.findOrder(paymentResponse.getOrderId());
        order.cancel(paymentResponse.getFailureMessages());
        orderSagaHelper.saveOrder(order);
        return order;
    }

    private SagaStatus[] getCurrentSagaStatus(PaymentStatus paymentStatus) {

        return switch (paymentStatus) {
            case COMPLETED -> new SagaStatus[]{SagaStatus.STARTED};
            // SagaStatus.STARTED -> when PaymentStatus = COMPLETED during rollback
            // only during success saga step, SagaStatus gets changed to SagaStatus.Processing
            case CANCELLED -> new SagaStatus[]{SagaStatus.PROCESSING};
            // SagaStatus.PROCESSING -> when PaymentStatus = CANCELLED during rollback
            case FAILED -> new SagaStatus[]{SagaStatus.STARTED, SagaStatus.PROCESSING};
            // SagaStatus can be in STARTED or PROCESSING state when the Payment is in Failed State
            default -> throw new IllegalStateException("Unexpected value: " + paymentStatus);
        };

    }


}
