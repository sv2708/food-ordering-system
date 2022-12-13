package org.sarav.food.payment.service.app.ports.input.message.listener;

import org.sarav.food.payment.service.app.dto.PaymentRequest;

public interface PaymentRequestMessageListener {

    void completePayment(PaymentRequest paymentRequest);

    void cancelPayment(PaymentRequest paymentRequest);

}
