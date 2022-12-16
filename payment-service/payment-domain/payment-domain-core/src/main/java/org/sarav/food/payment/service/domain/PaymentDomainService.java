package org.sarav.food.payment.service.domain;

import org.sarav.food.order.system.domain.event.publisher.DomainEventPublisher;
import org.sarav.food.payment.service.domain.entity.CreditEntry;
import org.sarav.food.payment.service.domain.entity.CreditHistory;
import org.sarav.food.payment.service.domain.entity.Payment;
import org.sarav.food.payment.service.domain.event.PaymentCancelledEvent;
import org.sarav.food.payment.service.domain.event.PaymentCompletedEvent;
import org.sarav.food.payment.service.domain.event.PaymentEvent;
import org.sarav.food.payment.service.domain.event.PaymentFailedEvent;

import java.util.List;

public interface PaymentDomainService {

    PaymentEvent validateAndInitiatePayment(Payment payment, CreditEntry creditEntry,
                                            List<CreditHistory> creditHistoryEntries, List<String> failureMessages,
                                            DomainEventPublisher<PaymentCompletedEvent> paymentCompletedEventPublisher,
                                            DomainEventPublisher<PaymentFailedEvent> paymentFailedEventPublisher

    );

    PaymentEvent validateAndCancelPayment(Payment payment, CreditEntry creditEntry,
                                          List<CreditHistory> creditHistoryEntries, List<String> failureMessages, DomainEventPublisher<PaymentFailedEvent> paymentFailedEventPublisher, DomainEventPublisher<PaymentCancelledEvent> paymentCancelledEventPublisher);
}
