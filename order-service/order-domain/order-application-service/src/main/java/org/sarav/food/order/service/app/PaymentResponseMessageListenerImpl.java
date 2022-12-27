package org.sarav.food.order.service.app;

import lombok.extern.slf4j.Slf4j;
import org.sarav.food.order.service.app.dto.message.PaymentResponse;
import org.sarav.food.order.service.app.ports.input.message.listener.payment.PaymentResponseMessageListener;
import org.sarav.food.order.service.domain.event.OrderPaidEvent;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import static org.sarav.food.order.service.domain.entity.Order.FAILURE_MSG_DELIMITER;

@Slf4j
@Component
@Validated
public class PaymentResponseMessageListenerImpl implements PaymentResponseMessageListener {

    private final OrderPaymentSagaStep orderPaymentSagaStep;

    public PaymentResponseMessageListenerImpl(OrderPaymentSagaStep orderPaymentSagaStep) {
        this.orderPaymentSagaStep = orderPaymentSagaStep;
    }

    @Override
    public void paymentCompleted(PaymentResponse paymentResponse) {
        log.info("Persisting the Order Status Changes for Order {} ", paymentResponse.getOrderId());
        OrderPaidEvent orderPaidEvent = orderPaymentSagaStep.success(paymentResponse);
        log.info("Publishing the Event for Order {} to restaurant for its approval", paymentResponse.getOrderId());
        orderPaidEvent.fire();
    }

    @Override
    public void paymentCancelled(PaymentResponse paymentResponse) {
        log.info("Persisting the Order Failed Status Changes for Order {} ", paymentResponse.getOrderId());
        orderPaymentSagaStep.rollback(paymentResponse);
        log.info("Order {} is cancelled with failure messages {}", paymentResponse.getOrderId(),
                String.join(FAILURE_MSG_DELIMITER, paymentResponse.getFailureMessages()));
    }
}
