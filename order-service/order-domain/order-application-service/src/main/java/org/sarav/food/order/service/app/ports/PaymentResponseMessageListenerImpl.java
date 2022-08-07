package org.sarav.food.order.service.app.ports;

import org.sarav.food.order.service.app.dto.message.PaymentResponse;
import org.sarav.food.order.service.app.ports.input.message.listener.payment.PaymentResponseMessageListener;

public class PaymentResponseMessageListenerImpl implements PaymentResponseMessageListener {
    @Override
    public void paymentCompleted(PaymentResponse paymentResponse) {

    }

    @Override
    public void paymentCancelled(PaymentResponse paymentResponse) {

    }
}
