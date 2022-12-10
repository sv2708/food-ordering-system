package org.sarav.food.payment.service.domain;

import org.sarav.food.payment.service.domain.entity.CreditEntry;
import org.sarav.food.payment.service.domain.entity.CreditHistory;
import org.sarav.food.payment.service.domain.entity.Payment;
import org.sarav.food.payment.service.domain.event.PaymentEvent;

import java.util.List;

public interface PaymentDomainService {

    PaymentEvent validateAndInitiatePayment(Payment payment, CreditEntry creditEntry,
                                            List<CreditHistory> creditHistoryEntries, List<String> failureMessages);

    PaymentEvent validateAndCancelPayment(Payment payment, CreditEntry creditEntry,
                                          List<CreditHistory> creditHistoryEntries, List<String> failureMessages);
}
