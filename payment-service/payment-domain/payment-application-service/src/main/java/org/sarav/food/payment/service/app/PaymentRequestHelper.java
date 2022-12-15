package org.sarav.food.payment.service.app;

import lombok.extern.slf4j.Slf4j;
import org.sarav.food.order.system.domain.valueobjects.CustomerId;
import org.sarav.food.payment.service.app.dto.PaymentRequest;
import org.sarav.food.payment.service.app.exception.PaymentApplicationServiceException;
import org.sarav.food.payment.service.app.mapper.PaymentDataMapper;
import org.sarav.food.payment.service.app.ports.output.repository.CreditEntryRepository;
import org.sarav.food.payment.service.app.ports.output.repository.CreditHistoryRepository;
import org.sarav.food.payment.service.app.ports.output.repository.PaymentRepository;
import org.sarav.food.payment.service.domain.PaymentDomainService;
import org.sarav.food.payment.service.domain.entity.CreditEntry;
import org.sarav.food.payment.service.domain.entity.CreditHistory;
import org.sarav.food.payment.service.domain.entity.Payment;
import org.sarav.food.payment.service.domain.event.PaymentEvent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class PaymentRequestHelper {

    private PaymentRepository paymentRepository;
    private CreditEntryRepository creditEntryRepository;
    private CreditHistoryRepository creditHistoryRepository;
    private PaymentDataMapper paymentDataMapper;
    private PaymentDomainService paymentDomainService;

    public PaymentRequestHelper(PaymentRepository paymentRepository,
                                CreditEntryRepository creditEntryRepository,
                                CreditHistoryRepository creditHistoryRepository,
                                PaymentDataMapper paymentDataMapper,
                                PaymentDomainService paymentDomainService) {
        this.paymentRepository = paymentRepository;
        this.creditEntryRepository = creditEntryRepository;
        this.creditHistoryRepository = creditHistoryRepository;
        this.paymentDataMapper = paymentDataMapper;
        this.paymentDomainService = paymentDomainService;
    }

    public PaymentEvent persistPayment(PaymentRequest paymentRequestModel) {

        Payment payment = paymentDataMapper.paymentRequestToPayment(paymentRequestModel);
        CreditEntry creditEntry = getCreditEntry(payment.getCustomerId());
        List<CreditHistory> creditHistories = getCreditHistories(payment.getCustomerId());
        List<String> failureMessages = new ArrayList<>();
        PaymentEvent paymentCompletedEvent = paymentDomainService.validateAndInitiatePayment(payment, creditEntry,
                creditHistories, failureMessages);
        paymentRepository.save(payment);
        if (failureMessages.size() == 0) {
            log.info("Payment Successfully initiated for order {}", payment.getOrderId().getValue());
            creditEntryRepository.save(creditEntry);
            creditHistoryRepository.save(creditHistories.get(creditHistories.size() - 1));
        } else {
            log.info("Payment initiation failed for order {}", payment.getOrderId().getValue());
        }
        return paymentCompletedEvent;
    }

    public PaymentEvent persistCancelPayment(PaymentRequest paymentRequest) {

        Payment payment = paymentDataMapper.paymentRequestToPayment(paymentRequest);
        log.info("Starting Rollback of payment for Order {}", payment.getOrderId().getValue());
        Optional<Payment> paymentPersisted = paymentRepository.findByOrderId(payment.getOrderId().getValue());
        if (paymentPersisted.isEmpty()) {
            log.error("Payment for OrderId " + payment.getOrderId().getValue() + " is not found.");
            throw new PaymentApplicationServiceException("Payment for OrderId " +
                    payment.getOrderId().getValue() + " is not found.");
        } else {
            log.debug("Found Payment {} for Order {}", payment.getId(), payment.getOrderId());
        }
        CreditEntry creditEntry = getCreditEntry(payment.getCustomerId());
        List<CreditHistory> creditHistories = getCreditHistories(payment.getCustomerId());
        List<String> failureMessages = new ArrayList<>();
        PaymentEvent paymentCancelledEvent = paymentDomainService.validateAndCancelPayment(payment, creditEntry, creditHistories, failureMessages);
        paymentRepository.save(payment);
        if (failureMessages.size() == 0) {
            log.info("Payment for OrderId " + payment.getOrderId().getValue() + " is cancelled successfully.");
            creditEntryRepository.save(creditEntry);
            creditHistoryRepository.save(creditHistories.get(creditHistories.size() - 1));
            log.error("Payment for OrderId " + payment.getOrderId().getValue() + " is cancelled successfully.");
        } else {
            log.error("Payment Cancellation for OrderId " + payment.getOrderId().getValue() + " validation is not successful.");
        }
        return paymentCancelledEvent;
    }


    private List<CreditHistory> getCreditHistories(CustomerId customerId) {
        var creditHistories = creditHistoryRepository.findByCustomerId(customerId.getValue());
        if (creditHistories.isEmpty()) {
            log.error("Could find Credit History for customer: {}", customerId.getValue());
            throw new PaymentApplicationServiceException("Could find Credit History for customer: " + customerId.getValue());
        }
        return creditHistories.get();
    }

    private CreditEntry getCreditEntry(CustomerId customerId) {
        var creditEntry = creditEntryRepository.findByCustomerId(customerId.getValue());
        if (creditEntry.isEmpty()) {
            log.error("Could find credit entry for customer: {}", customerId.getValue());
            throw new PaymentApplicationServiceException("Could find credit entry for customer: " + customerId.getValue());
        }
        return creditEntry.get();
    }

}
