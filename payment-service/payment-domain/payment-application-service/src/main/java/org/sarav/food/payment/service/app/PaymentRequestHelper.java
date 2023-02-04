package org.sarav.food.payment.service.app;

import lombok.extern.slf4j.Slf4j;
import org.sarav.food.order.system.domain.valueobjects.CustomerId;
import org.sarav.food.order.system.domain.valueobjects.PaymentStatus;
import org.sarav.food.order.system.outbox.OutboxStatus;
import org.sarav.food.payment.service.app.dto.PaymentRequest;
import org.sarav.food.payment.service.app.exception.PaymentApplicationServiceException;
import org.sarav.food.payment.service.app.mapper.PaymentDataMapper;
import org.sarav.food.payment.service.app.outbox.OrderOutboxHelper;
import org.sarav.food.payment.service.app.outbox.model.OrderOutboxMessage;
import org.sarav.food.payment.service.app.ports.output.message.publisher.PaymentResponseMessagePublisher;
import org.sarav.food.payment.service.app.ports.output.repository.CreditEntryRepository;
import org.sarav.food.payment.service.app.ports.output.repository.CreditHistoryRepository;
import org.sarav.food.payment.service.app.ports.output.repository.PaymentRepository;
import org.sarav.food.payment.service.domain.PaymentDomainService;
import org.sarav.food.payment.service.domain.entity.CreditEntry;
import org.sarav.food.payment.service.domain.entity.CreditHistory;
import org.sarav.food.payment.service.domain.entity.Payment;
import org.sarav.food.payment.service.domain.event.PaymentEvent;
import org.sarav.food.payment.service.domain.exception.PaymentNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class PaymentRequestHelper {

    private PaymentRepository paymentRepository;
    private CreditEntryRepository creditEntryRepository;
    private CreditHistoryRepository creditHistoryRepository;
    private PaymentDataMapper paymentDataMapper;
    private PaymentDomainService paymentDomainService;
    private OrderOutboxHelper orderOutboxHelper;
    private PaymentResponseMessagePublisher paymentResponseMessagePublisher;

    public PaymentRequestHelper(PaymentRepository paymentRepository,
                                CreditEntryRepository creditEntryRepository,
                                CreditHistoryRepository creditHistoryRepository,
                                PaymentDataMapper paymentDataMapper,
                                PaymentDomainService paymentDomainService,
                                OrderOutboxHelper orderOutboxHelper,
                                PaymentResponseMessagePublisher paymentResponseMessagePublisher) {
        this.paymentRepository = paymentRepository;
        this.creditEntryRepository = creditEntryRepository;
        this.creditHistoryRepository = creditHistoryRepository;
        this.paymentDataMapper = paymentDataMapper;
        this.paymentDomainService = paymentDomainService;
        this.orderOutboxHelper = orderOutboxHelper;
    }

    @Transactional
    public void persistPayment(PaymentRequest paymentRequestModel) {

        /**
         * Before Processing the PaymentRequest received,
         * first the check should be made if it's a retry or first try for the payment.
         * Payment Could be retried by order-service if the processing for the PaymentCompleted message failed and
         * the message will be republished by the order-service as the outbox status at order-service is not yet updated.
         * So in that case we just need to republish the message as the payment-service has already processed the message
         * and persisted the payment and saved the event in the outbox table.
         */
        if (publishIfOutboxMessageAlreadyProcessedForPayment(paymentRequestModel, PaymentStatus.COMPLETED)) {
            log.info("Payment Request {} has already been processed and saved in outbox table",
                    paymentRequestModel.getId());
            return;
        }

        Payment payment = paymentDataMapper.paymentRequestToPayment(paymentRequestModel);
        CreditEntry creditEntry = getCreditEntry(payment.getCustomerId());
        List<CreditHistory> creditHistories = getCreditHistories(payment.getCustomerId());
        List<String> failureMessages = new ArrayList<>();
        PaymentEvent paymentEvent = paymentDomainService.validateAndInitiatePayment(payment, creditEntry,
                creditHistories, failureMessages);
        paymentRepository.save(payment);
        orderOutboxHelper.saveOrderOutboxMessage(
                paymentDataMapper.paymentEventToOrderEventPayload(paymentEvent),
                payment.getPaymentStatus(), OutboxStatus.STARTED,
                UUID.fromString(paymentRequestModel.getSagaId())
        );
        if (failureMessages.size() == 0) {
            log.info("Payment Successfully initiated for order {}", payment.getOrderId().getValue());
            creditEntryRepository.save(creditEntry);
            creditHistoryRepository.save(creditHistories.get(creditHistories.size() - 1));
        } else {
            log.info("Payment initiation failed for order {}", payment.getOrderId().getValue());
        }
    }

    @Transactional
    public void persistCancelPayment(PaymentRequest paymentRequest) {

        /**
         * Before Processing the PaymentRequest received,
         * first the check should be made if it's a retry or first try for the payment cancel.
         * Payment Could be retried by order-service if the processing for the PaymentCancelled message failed and
         * the message will be republished by the order-service as the outbox status at order-service is not yet updated.
         * So in that case we just need to republish the message as the payment-service has already processed the message
         * and persisted the payment and saved the event in the outbox table.
         */
        if (publishIfOutboxMessageAlreadyProcessedForPayment(paymentRequest, PaymentStatus.CANCELLED)) {
            log.info("Payment Request {} has already been processed and saved in outbox table",
                    paymentRequest.getId());
            return;
        }

        Payment payment = paymentDataMapper.paymentRequestToPayment(paymentRequest);
        log.info("Starting Rollback of payment for Order {}", payment.getOrderId().getValue());
        Optional<Payment> paymentPersisted = paymentRepository.findByOrderId(payment.getOrderId().getValue());
        if (paymentPersisted.isEmpty()) {
            log.error("Payment for OrderId " + payment.getOrderId().getValue() + " is not found.");
            throw new PaymentNotFoundException("Payment for OrderId " +
                    payment.getOrderId().getValue() + " is not found.");
        } else {
            log.debug("Found Payment {} for Order {}", payment.getId(), payment.getOrderId());
        }
        CreditEntry creditEntry = getCreditEntry(payment.getCustomerId());
        List<CreditHistory> creditHistories = getCreditHistories(payment.getCustomerId());
        List<String> failureMessages = new ArrayList<>();
        PaymentEvent paymentEvent = paymentDomainService.validateAndCancelPayment(payment, creditEntry, creditHistories, failureMessages);
        paymentRepository.save(payment);
        orderOutboxHelper.saveOrderOutboxMessage(
                paymentDataMapper.paymentEventToOrderEventPayload(paymentEvent),
                paymentEvent.getPayment().getPaymentStatus(),
                OutboxStatus.STARTED, UUID.fromString(paymentRequest.getSagaId()));
        if (failureMessages.size() == 0) {
            log.info("Payment for OrderId " + payment.getOrderId().getValue() + " is cancelled successfully.");
            creditEntryRepository.save(creditEntry);
            creditHistoryRepository.save(creditHistories.get(creditHistories.size() - 1));
            log.error("Payment for OrderId " + payment.getOrderId().getValue() + " is cancelled successfully.");
        } else {
            log.error("Payment Cancellation for OrderId " + payment.getOrderId().getValue() + " validation is not successful.");
        }
    }

    /**
     * This method checks if the Payment Request is already processed by the payment-service
     * and if that is the case, then republish the payment response in the payment response topic.
     *
     * @param paymentRequest
     * @param paymentStatus  PaymentStatus that is Completed/Cancelled
     * @return
     */
    private boolean publishIfOutboxMessageAlreadyProcessedForPayment(PaymentRequest paymentRequest,
                                                                     PaymentStatus paymentStatus) {
        Optional<OrderOutboxMessage> outboxMessageForProcessedPayment =
                orderOutboxHelper.getCompletedOrderOutboxMessageBySagaIdAndPaymentStatus(
                        UUID.fromString(paymentRequest.getSagaId()), paymentStatus
                );
        if (outboxMessageForProcessedPayment.isPresent()) {
            paymentResponseMessagePublisher.publish(outboxMessageForProcessedPayment.get(),
                    orderOutboxHelper::updateOutboxMessageStatus);
            return true;
        }
        return false;
    }

    private List<CreditHistory> getCreditHistories(CustomerId customerId) {
        var creditHistories = creditHistoryRepository.findByCustomerId(customerId);
        if (creditHistories.isEmpty()) {
            log.error("Could find Credit History for customer: {}", customerId.getValue());
            throw new PaymentApplicationServiceException("Could find Credit History for customer: " + customerId.getValue());
        }
        return creditHistories.get();
    }

    private CreditEntry getCreditEntry(CustomerId customerId) {
        var creditEntry = creditEntryRepository.findByCustomerId(customerId);
        if (creditEntry.isEmpty()) {
            log.error("Could find credit entry for customer: {}", customerId.getValue());
            throw new PaymentApplicationServiceException("Could find credit entry for customer: " + customerId.getValue());
        }
        return creditEntry.get();
    }

}
