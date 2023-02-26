package org.sarav.food.order.service.messaging.listener.kafka;

import lombok.extern.slf4j.Slf4j;
import org.sarav.food.order.CustomerAvroModel;
import org.sarav.food.order.service.app.ports.input.message.listener.customer.CustomerMessageListener;
import org.sarav.food.order.service.messaging.mapper.OrderMessagingDataMapper;
import org.sarav.food.order.system.kafka.service.KafkaConsumer;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class CustomerKafkaListener implements KafkaConsumer<CustomerAvroModel> {

    private final CustomerMessageListener customerMessageListener;
    private final OrderMessagingDataMapper orderMessagingDataMapper;

    public CustomerKafkaListener(CustomerMessageListener customerMessageListener, OrderMessagingDataMapper orderMessagingDataMapper) {
        this.customerMessageListener = customerMessageListener;
        this.orderMessagingDataMapper = orderMessagingDataMapper;
    }

    @Override
    @KafkaListener(id = "${kafka-consumer-config.customer-consumer-group-id}",
            topics = "${order-service.customer-topic-name}")
    public void receive(@Payload List<CustomerAvroModel> messages,
                        @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) List<String> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION_ID) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {

        log.info("Received {} messages with headers of key: {}, Partitions: {}, Offsets: {} ",
                messages.size(), keys.toString(), partitions.toString(), offsets.toString());
        messages.forEach(message -> {
            customerMessageListener.customerCreated(orderMessagingDataMapper.customerAvroModelToCustomerModel(message));
        });
    }
}
