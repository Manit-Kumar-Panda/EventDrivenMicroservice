package com.microservices.demo.kafka.producer.config.service;

import org.apache.avro.specific.SpecificRecordBase;

import java.io.Serializable;

/**
 * Created by manitkumarpanda on 07/05/25
 */

public interface KafkaProducer<K extends Serializable, V extends SpecificRecordBase> {

  void send(String topicName, K key, V message);

}
