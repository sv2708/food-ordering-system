package org.sarav.food.order.service.app.ports.input.message.listener.payment;

import org.sarav.food.order.service.app.dto.message.PaymentResponse;

public interface PaymentResponseMessageListener {

    void paymentCompleted(PaymentResponse paymentResponse);

    void paymentCancelled(PaymentResponse paymentResponse);

}
