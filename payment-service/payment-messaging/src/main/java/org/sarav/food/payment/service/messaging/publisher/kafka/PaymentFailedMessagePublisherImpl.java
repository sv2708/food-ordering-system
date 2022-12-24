package org.sarav.food.payment.service.messaging.publisher.kafka;

import lombok.extern.slf4j.Slf4j;
import org.sarav.food.order.PaymentResponseAvroModel;
import org.sarav.food.order.system.kafka.KafkaMessageHelper;
import org.sarav.food.order.system.kafka.service.KafkaProducer;
import org.sarav.food.payment.service.app.config.PaymentServiceConfigData;
import org.sarav.food.payment.service.app.ports.output.message.publisher.PaymentFailedMessagePublisher;
import org.sarav.food.payment.service.domain.event.PaymentFailedEvent;
import org.sarav.food.payment.service.messaging.mapper.PaymentMessagingDataMapper;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PaymentFailedMessagePublisherImpl implements PaymentFailedMessagePublisher {

    private final PaymentMessagingDataMapper paymentMessagingDataMapper;
    private final KafkaProducer<String, PaymentResponseAvroModel> paymentResponseAvroModelKafkaProducer;
    private final PaymentServiceConfigData paymentServiceConfigData;
    private final KafkaMessageHelper kafkaMessageHelper;

    public PaymentFailedMessagePublisherImpl(PaymentMessagingDataMapper paymentMessagingDataMapper, KafkaProducer<String, PaymentResponseAvroModel> paymentResponseAvroModelKafkaProducer, PaymentServiceConfigData paymentServiceConfigData, KafkaMessageHelper kafkaMessageHelper) {
        this.paymentMessagingDataMapper = paymentMessagingDataMapper;
        this.paymentResponseAvroModelKafkaProducer = paymentResponseAvroModelKafkaProducer;
        this.paymentServiceConfigData = paymentServiceConfigData;
        this.kafkaMessageHelper = kafkaMessageHelper;
    }

    @Override
    public void publish(PaymentFailedEvent paymentFailedEvent) {
        String orderId = paymentFailedEvent.getPayment().getOrderId().getValue().toString();
        log.info("Payment Failed and publishing the event message for the order {}", orderId);
        PaymentResponseAvroModel paymentResponseAvroModel = paymentMessagingDataMapper.paymentEventToPaymentResponseAvroModel(paymentFailedEvent);
        paymentResponseAvroModelKafkaProducer.sendMessage(paymentServiceConfigData.getPaymentResponseTopicName(),
                orderId, paymentResponseAvroModel,
                kafkaMessageHelper.getKafkaCallback(paymentServiceConfigData.getPaymentResponseTopicName(),
                        paymentResponseAvroModel, orderId,
                        "PaymentResponseAvroModel")
        );
    }


}
