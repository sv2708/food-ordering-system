package org.sarav.food.payment.service.messaging.listener.kafka;

import lombok.extern.slf4j.Slf4j;
import org.sarav.food.order.OrderPaymentStatus;
import org.sarav.food.order.PaymentRequestAvroModel;
import org.sarav.food.order.system.kafka.service.KafkaConsumer;
import org.sarav.food.payment.service.app.dto.PaymentRequest;
import org.sarav.food.payment.service.app.ports.input.message.listener.PaymentRequestMessageListener;
import org.sarav.food.payment.service.messaging.mapper.PaymentMessagingDataMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class PaymentRequestKafkaListener implements KafkaConsumer<PaymentRequestAvroModel> {

    private final PaymentRequestMessageListener paymentRequestMessageListener;
    private final PaymentMessagingDataMapper paymentMessagingDataMapper;

    public PaymentRequestKafkaListener(PaymentRequestMessageListener paymentRequestMessageListener,
                                       PaymentMessagingDataMapper paymentMessagingDataMapper) {
        this.paymentRequestMessageListener = paymentRequestMessageListener;
        this.paymentMessagingDataMapper = paymentMessagingDataMapper;
    }

    @Override
    @KafkaListener(id = "${kafka-consumer-config.payment-consumer-group-id}",
            topics = "${payment-service.payment-request-topic-name}")
    public void receive(@Payload List<PaymentRequestAvroModel> messages,
                        @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) List<String> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION_ID) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {
        log.info("{} Messages received for keys {} in partitions: {} with offsets: {}",
                messages.size(), keys, partitions, offsets);

        messages.forEach(paymentRequestAvroModel -> {
            PaymentRequest paymentRequest = paymentMessagingDataMapper
                    .paymentRequestAvroModelToPaymentRequest(paymentRequestAvroModel);
            if (paymentRequestAvroModel.getOrderPaymentStatus() == OrderPaymentStatus.PENDING) {
                paymentRequestMessageListener
                        .completePayment(paymentRequest);
            } else if (paymentRequestAvroModel.getOrderPaymentStatus() == OrderPaymentStatus.ORDER_CANCELLED) {
                paymentRequestMessageListener.cancelPayment(paymentRequest);
            }
        });

    }
}
