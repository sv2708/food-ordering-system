package org.sarav.food.order.service.messaging.publisher.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.sarav.food.order.PaymentRequestAvroModel;
import org.sarav.food.order.service.app.config.OrderServiceConfigData;
import org.sarav.food.order.service.app.ports.output.message.publisher.payment.OrderCreatedPaymentRequestMessagePublisher;
import org.sarav.food.order.service.domain.event.OrderCreatedEvent;
import org.sarav.food.order.service.messaging.mapper.OrderMessagingDataMapper;
import org.sarav.food.order.system.kafka.service.KafkaProducer;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Slf4j
@Component
public class CreateOrderKafkaMessagePublisher implements OrderCreatedPaymentRequestMessagePublisher {

    private final OrderServiceConfigData orderServiceConfigData;
    private final KafkaProducer<String, PaymentRequestAvroModel> kafkaProducer;
    private final OrderMessagingDataMapper orderMessagingDataMapper;

    public CreateOrderKafkaMessagePublisher(OrderServiceConfigData orderServiceConfigData, KafkaProducer<String, PaymentRequestAvroModel> kafkaProducer, OrderMessagingDataMapper orderMessagingDataMapper) {
        this.orderServiceConfigData = orderServiceConfigData;
        this.kafkaProducer = kafkaProducer;
        this.orderMessagingDataMapper = orderMessagingDataMapper;
    }

    @Override
    public void publish(OrderCreatedEvent domainEvent) {

        try {
            var paymentRequestAvroModel = orderMessagingDataMapper
                    .orderCreatedEventToPaymentRequestAvroModel(domainEvent);

            kafkaProducer.sendMessage(orderServiceConfigData.getPaymentRequestTopicName(),
                    domainEvent.getOrder().getId().getValue().toString(),
                    paymentRequestAvroModel,
                    getKafkaCallback(orderServiceConfigData.getPaymentResponseTopicName(), paymentRequestAvroModel)
            );
            log.info("Order Created Event message successfully sent for Order Id {} on topic {}",
                    paymentRequestAvroModel.getOrderId(), orderServiceConfigData.getPaymentRequestTopicName());
        } catch (Exception e) {
            log.error("Error Occurred while sending OrderCreatedEvent message to Kafka " +
                    e.getMessage());
        }

    }

    private ListenableFutureCallback<SendResult<String, PaymentRequestAvroModel>> getKafkaCallback(String paymentResponseTopicName, PaymentRequestAvroModel paymentRequestAvroModel) {

        return new ListenableFutureCallback<SendResult<String, PaymentRequestAvroModel>>() {
            @Override
            public void onFailure(Throwable ex) {
                log.error("Error Occurred while sending {} to topic {}", paymentRequestAvroModel.toString(), paymentResponseTopicName);
                log.error(ex.getMessage());
            }

            @Override
            public void onSuccess(SendResult<String, PaymentRequestAvroModel> result) {
                RecordMetadata recordMetadata = result.getRecordMetadata();
                log.info("Received Successful Response from Kafka for OrderId: {} on Topic: {}, " +
                                "  Partition: {}, Offset {}, Timestamp {} ",
                        paymentRequestAvroModel.getOrderId(), recordMetadata.topic(),
                        recordMetadata.partition(), recordMetadata.offset(), recordMetadata.timestamp());

            }
        };

    }
}
