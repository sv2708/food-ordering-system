package org.sarav.food.order.service.messaging.publisher.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Slf4j
@Component
public class OrderKafkaMesssageHelper {


    public <T> ListenableFutureCallback<SendResult<String, T>>
    getKafkaCallback(String topicName, T requestAvroModel, String orderId, String requestAvroModelName) {

        return new ListenableFutureCallback<>() {
            @Override
            public void onFailure(Throwable ex) {
                log.error("Failed Publishing K(OrderID): {}, V({}):{}", orderId, requestAvroModelName, requestAvroModel.toString());
                log.error("Error Occurred while sending {} {} to topic {}", requestAvroModelName, requestAvroModel.toString(), topicName);
                log.error(ex.getMessage());
            }

            @Override
            public void onSuccess(SendResult<String, T> result) {
                RecordMetadata recordMetadata = result.getRecordMetadata();
                log.info("Published K(OrderID): {}, V({}):{}", orderId, requestAvroModelName, requestAvroModel.toString());
                log.info("Received Successful Response from Kafka for publishing OrderId: {} on Topic: {}, " +
                                "  Partition: {}, Offset {}, Timestamp {} ",
                        orderId, recordMetadata.topic(),
                        recordMetadata.partition(), recordMetadata.offset(), recordMetadata.timestamp());

            }
        };

    }


}
