package org.sarav.food.order.system.kafka.service;


import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.io.Serializable;

public interface KafkaProducer<K extends Serializable, V extends SpecificRecordBase> {

    void sendMessage(String topic, K key, V message, ListenableFutureCallback<SendResult<K, V>> callback);

}
