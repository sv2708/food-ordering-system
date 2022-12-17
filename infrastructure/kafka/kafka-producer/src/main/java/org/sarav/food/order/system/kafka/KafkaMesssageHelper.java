package org.sarav.food.order.system.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Slf4j
@Component
public class KafkaMesssageHelper {


    public <T> ListenableFutureCallback<SendResult<String, T>>
    getKafkaCallback(String topicName, T avroModel, String id, String avroModelName) {

        return new ListenableFutureCallback<>() {
            @Override
            public void onFailure(Throwable ex) {
                log.error("Failed Publishing K(ID): {}, V({}):{}", id, avroModelName, avroModel.toString());
                log.error("Error Occurred while sending {} {} to topic {}", avroModelName, avroModel.toString(), topicName);
                log.error(ex.getMessage());
            }

            @Override
            public void onSuccess(SendResult<String, T> result) {
                RecordMetadata recordMetadata = result.getRecordMetadata();
                log.info("Published K(ID): {}, V({}):{}", id, avroModelName, avroModel.toString());
                log.info("Received Successful Response from Kafka for publishing ID: {} on Topic: {}, " +
                                "  Partition: {}, Offset {}, Timestamp {} ",
                        id, recordMetadata.topic(),
                        recordMetadata.partition(), recordMetadata.offset(), recordMetadata.timestamp());

            }
        };

    }


}
