package org.sarav.food.payment.service.app;

import lombok.extern.slf4j.Slf4j;
import org.sarav.food.payment.service.app.dto.PaymentRequest;
import org.sarav.food.payment.service.app.ports.input.message.listener.PaymentRequestMessageListener;
import org.sarav.food.payment.service.domain.event.PaymentEvent;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PaymentRequestMessageListenerImpl implements PaymentRequestMessageListener {

    private final PaymentRequestHelper paymentRequestHelper;

    public PaymentRequestMessageListenerImpl(PaymentRequestHelper paymentRequestHelper) {
        this.paymentRequestHelper = paymentRequestHelper;
    }

    @Override
    public void completePayment(PaymentRequest paymentRequest) {
        PaymentEvent paymentEvent = paymentRequestHelper.persistPayment(paymentRequest);
        fireEvent(paymentEvent);
    }

    @Override
    public void cancelPayment(PaymentRequest paymentRequest) {
        PaymentEvent paymentEvent = paymentRequestHelper.persistCancelPayment(paymentRequest);
        fireEvent(paymentEvent);
    }

    private void fireEvent(PaymentEvent paymentEvent) {
        log.info("Publishing Payment Event for Payment {}", paymentEvent.getPayment().getId().getValue());
        paymentEvent.fire();
    }
}
