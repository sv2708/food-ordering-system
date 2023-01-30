package org.sarav.food.order.system.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.sarav.food.order.service.domain.exception.OrderDomainException;
import org.sarav.food.order.system.outbox.OutboxStatus;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.function.BiConsumer;

@Slf4j
@Component
public class KafkaMessageHelper {

    private final ObjectMapper objectMapper;

    public KafkaMessageHelper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <T, U> ListenableFutureCallback<SendResult<String, T>>
    getKafkaCallback(String topicName, T avroModel,
                     U outboxMessage, BiConsumer<U, OutboxStatus> updateOutboxMessage,
                     String id, String avroModelName) {

        return new ListenableFutureCallback<>() {
            @Override
            public void onFailure(Throwable ex) {
                log.error("Failed Publishing K(ID): {}, V({}):{}", id, avroModelName, avroModel.toString());
                log.error("Error Occurred while sending {} {} to topic {}", avroModelName, avroModel.toString(), topicName);
                log.error(ex.getMessage());
                updateOutboxMessage.accept(outboxMessage, OutboxStatus.FAILED);
            }

            @Override
            public void onSuccess(SendResult<String, T> result) {
                RecordMetadata recordMetadata = result.getRecordMetadata();
                log.info("Published K(ID): {}, V({}):{}", id, avroModelName, avroModel.toString());
                log.info("Received Successful Response from Kafka for publishing ID: {} on Topic: {}, " +
                                "  Partition: {}, Offset {}, Timestamp {} ",
                        id, recordMetadata.topic(),
                        recordMetadata.partition(), recordMetadata.offset(), recordMetadata.timestamp());
                updateOutboxMessage.accept(outboxMessage, OutboxStatus.COMPLETED);
            }
        };
    }

    public <T> T getOrderEventPayload(String payload, Class<T> type) {
        try {
            return objectMapper.readValue(payload, type);
        } catch (JsonProcessingException e) {
            throw new OrderDomainException("Error Occurred while parsing the JSON " + e.getMessage());
        }
    }


}
