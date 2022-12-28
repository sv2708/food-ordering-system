package org.sarav.food.payment.service.domain;

import lombok.extern.slf4j.Slf4j;
import org.sarav.food.order.system.domain.DomainConstants;
import org.sarav.food.order.system.domain.event.publisher.DomainEventPublisher;
import org.sarav.food.order.system.domain.valueobjects.Money;
import org.sarav.food.order.system.domain.valueobjects.PaymentStatus;
import org.sarav.food.payment.service.domain.entity.CreditEntry;
import org.sarav.food.payment.service.domain.entity.CreditHistory;
import org.sarav.food.payment.service.domain.entity.Payment;
import org.sarav.food.payment.service.domain.entity.TransactionType;
import org.sarav.food.payment.service.domain.event.PaymentCancelledEvent;
import org.sarav.food.payment.service.domain.event.PaymentCompletedEvent;
import org.sarav.food.payment.service.domain.event.PaymentEvent;
import org.sarav.food.payment.service.domain.event.PaymentFailedEvent;
import org.sarav.food.payment.service.domain.valueobjects.CreditHistoryId;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
public class PaymentDomainServiceImpl implements PaymentDomainService {


    @Override
    public PaymentEvent validateAndInitiatePayment(Payment payment, CreditEntry creditEntry,
                                                   List<CreditHistory> creditHistoryEntries,
                                                   List<String> failureMessages,
                                                   DomainEventPublisher<PaymentCompletedEvent>
                                                           paymentCompletedEventPublisher,
                                                   DomainEventPublisher<PaymentFailedEvent> paymentFailedEventPublisher) {

        payment.validatePayment(failureMessages);
        payment.initializePayment(); // set id and createdAt fields
//        if (validateCreditEntry(payment, creditEntry, failureMessages)) {
//            subtractCreditEntry(payment, creditEntry);
//            updateCreditHistory(payment, TransactionType.DEBIT, creditHistoryEntries);
//            validateCreditHistory(payment, creditEntry, creditHistoryEntries, failureMessages);
//        }
        if (failureMessages.size() == 0) {
            payment.updateStatus(PaymentStatus.COMPLETED);
            return new PaymentCompletedEvent(payment, ZonedDateTime.now(ZoneId.of(DomainConstants.UTC)),
                    failureMessages,
                    paymentCompletedEventPublisher);
        }
        return new PaymentFailedEvent(payment, ZonedDateTime.now(ZoneId.of(DomainConstants.UTC)), failureMessages, paymentFailedEventPublisher);
    }

    private void validateCreditHistory(Payment payment, CreditEntry creditEntry,
                                       List<CreditHistory> creditHistoryEntries, List<String> failureMessages) {


        Money totalCreditAmount = getTotalAmount(creditHistoryEntries, TransactionType.CREDIT);
        Money totalDebitAmount = getTotalAmount(creditHistoryEntries, TransactionType.DEBIT);

        if (totalDebitAmount.isGreaterThan(totalCreditAmount)) {
            log.error("Total Debit amount {} is greater than total Credit amount {} ",
                    totalDebitAmount.getAmount(), totalCreditAmount.getAmount());
            failureMessages.add("Total debit amount is greater than total credit amount");
        }

        if (!totalCreditAmount.subtract(totalDebitAmount).equals(creditEntry.getTotalCreditAmount())) {
            log.error("There is an imbalance in total credit and debit amounts with respect to CreditEntry amount");
            log.error("Total Credit amount: {}", totalCreditAmount.getAmount());
            log.error("Total Debit amount: {}", totalDebitAmount.getAmount());
            log.error("Credit Entry amount: {}", creditEntry.getTotalCreditAmount());
            failureMessages.add("There is an imbalance in total credit and" +
                    " debit amounts with respect to CreditEntry amount");
        }
    }

    private Money getTotalAmount(List<CreditHistory> creditHistoryEntries, TransactionType transactionType) {
        return creditHistoryEntries.stream()
                .filter(creditHistory -> creditHistory.getTransactionType().equals(transactionType))
                .map(CreditHistory::getAmount).reduce(new Money("0"), Money::add);
    }

    private void updateCreditHistory(Payment payment, TransactionType transactionType,
                                     List<CreditHistory> creditHistoryEntries) {

        creditHistoryEntries.add(CreditHistory.builder().id(new CreditHistoryId(UUID.randomUUID()))
                .customerId(payment.getCustomerId())
                .amount(payment.getAmount())
                .transactionType(transactionType)
                .build()
        );

    }

    private void subtractCreditEntry(Payment payment, CreditEntry creditEntry) {
        creditEntry.subtractCreditAmount(payment.getAmount());
    }

    private boolean validateCreditEntry(Payment payment, CreditEntry creditEntry, List<String> failureMessages) {

        if (payment.getAmount().isGreaterThan(creditEntry.getTotalCreditAmount())) {
            log.error("Payment Amount is greater than the available credit amount in the account");
            failureMessages.add("Payment Amount is greater than the available credit amount in the account");
            return false;
        }
        return true;
    }

    /**
     * Tries to cancel the payment if possible, returns PaymentCancelledEvent with status updated as CANCELLED
     * if not returns PaymentFailedEvent with status updated as FAILED
     *
     * @param payment
     * @param creditEntry
     * @param creditHistoryEntries
     * @param failureMessages
     * @param paymentFailedEventPublisher
     * @param paymentCancelledEventPublisher
     * @return
     */
    @Override
    public PaymentEvent validateAndCancelPayment(Payment payment, CreditEntry creditEntry,
                                                 List<CreditHistory> creditHistoryEntries,
                                                 List<String> failureMessages,
                                                 DomainEventPublisher<PaymentFailedEvent> paymentFailedEventPublisher,
                                                 DomainEventPublisher<PaymentCancelledEvent> paymentCancelledEventPublisher) {
        payment.validatePayment(failureMessages);
        if (failureMessages.isEmpty()) {
            creditEntry.addCreditAmount(payment.getAmount());
            updateCreditHistory(payment, TransactionType.CREDIT, creditHistoryEntries);
            log.info("Payment is cancelled successfully for payment {} and order {}",
                    payment.getId(), payment.getOrderId());
            payment.setPaymentStatus(PaymentStatus.CANCELLED);
        } else {
            log.error("Error occurred while cancelling the payment {} for order {} ",
                    payment.getId(), payment.getOrderId());
            payment.updateStatus(PaymentStatus.FAILED);
            return new PaymentFailedEvent(payment, ZonedDateTime.now(ZoneId.of(DomainConstants.UTC)),
                    failureMessages, paymentFailedEventPublisher);
        }
        return new PaymentCancelledEvent(payment, ZonedDateTime.now(ZoneId.of(DomainConstants.UTC)), failureMessages, paymentCancelledEventPublisher);
    }
}
