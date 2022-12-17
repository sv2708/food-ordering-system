package org.sarav.food.payment.service.messaging.publisher.kafka;

import lombok.extern.slf4j.Slf4j;
import org.sarav.food.order.PaymentResponseAvroModel;
import org.sarav.food.order.system.kafka.KafkaMesssageHelper;
import org.sarav.food.order.system.kafka.service.KafkaProducer;
import org.sarav.food.payment.service.app.config.PaymentServiceConfigData;
import org.sarav.food.payment.service.app.ports.output.message.publisher.PaymentCancelledMessagePublisher;
import org.sarav.food.payment.service.domain.event.PaymentCancelledEvent;
import org.sarav.food.payment.service.messaging.mapper.PaymentMessagingDataMapper;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PaymentCancelledMessagePublisherImpl implements PaymentCancelledMessagePublisher {

    private final PaymentMessagingDataMapper paymentMessagingDataMapper;
    private final KafkaProducer<String, PaymentResponseAvroModel> paymentResponseAvroModelKafkaProducer;
    private final PaymentServiceConfigData paymentServiceConfigData;
    private final KafkaMesssageHelper kafkaMesssageHelper;

    public PaymentCancelledMessagePublisherImpl(PaymentMessagingDataMapper paymentMessagingDataMapper, KafkaProducer<String, PaymentResponseAvroModel> paymentResponseAvroModelKafkaProducer, PaymentServiceConfigData paymentServiceConfigData, KafkaMesssageHelper kafkaMesssageHelper) {
        this.paymentMessagingDataMapper = paymentMessagingDataMapper;
        this.paymentResponseAvroModelKafkaProducer = paymentResponseAvroModelKafkaProducer;
        this.paymentServiceConfigData = paymentServiceConfigData;
        this.kafkaMesssageHelper = kafkaMesssageHelper;
    }

    @Override
    public void publish(PaymentCancelledEvent paymentCancelledEvent) {
        String orderId = paymentCancelledEvent.getPayment().getOrderId().getValue().toString();
        log.info("Payment Cancelled and publishing the event message for the order {}", orderId);
        PaymentResponseAvroModel paymentResponseAvroModel = paymentMessagingDataMapper.paymentEventToPaymentResponseAvroModel(paymentCancelledEvent);
        paymentResponseAvroModelKafkaProducer.sendMessage(paymentServiceConfigData.getPaymentResponseTopicName(),
                orderId, paymentResponseAvroModel,
                kafkaMesssageHelper.getKafkaCallback(paymentServiceConfigData.getPaymentResponseTopicName(),
                        paymentResponseAvroModel, orderId,
                        "PaymentResponseAvroModel")
        );
    }


}
