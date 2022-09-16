package org.sarav.food.order.system.kafka.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.sarav.food.order.system.kafka.exception.KafkaProducerException;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

import javax.annotation.PreDestroy;
import java.io.Serializable;

@Slf4j
@Component
public class KafkaProducerImpl<K extends Serializable, V extends SpecificRecordBase> implements KafkaProducer<K, V> {

    private final KafkaTemplate<K, V> kafkaTemplate;

    public KafkaProducerImpl(KafkaTemplate<K, V> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void sendMessage(String topic, K key, V message, ListenableFutureCallback<SendResult<K, V>> callback) {

        try {
            log.info("Sending message-> {} to topic {} ", message, topic);
            var listenableFuture = kafkaTemplate.send(topic, key, message);
            listenableFuture.addCallback(callback);
        } catch (KafkaException e) {
            log.error("Error Occurred {}", e.getMessage());
            throw new KafkaProducerException(e.getMessage());
        }

    }

    @PreDestroy
    public void close() {
        if (kafkaTemplate != null) {
            log.info("Shutting down Kafka ProducerTemplate...");
            kafkaTemplate.destroy();
        }
    }
}
