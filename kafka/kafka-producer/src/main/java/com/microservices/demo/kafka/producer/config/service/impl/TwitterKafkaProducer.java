package com.microservices.demo.kafka.producer.config.service.impl;

import com.google.common.util.concurrent.ListenableFutureTask;
import com.microservices.demo.kafka.avro.model.TwitterAvroModel;
import com.microservices.demo.kafka.producer.config.service.KafkaProducer;
import jakarta.annotation.PreDestroy;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * Created by manitkumarpanda on 07/05/25
 */

@Service
public class TwitterKafkaProducer implements KafkaProducer<Long, TwitterAvroModel> {

  public static final Logger LOG = LoggerFactory.getLogger(TwitterKafkaProducer.class);

  @Autowired
  private KafkaTemplate<Long, TwitterAvroModel> kafkaTemplate;


  public void send(String topicName, Long key, TwitterAvroModel message) {
    LOG.info("Sending message='{}' to topic='{}'", message, topicName);

    CompletableFuture<SendResult<Long, TwitterAvroModel>> kafkaResultFuture =
        kafkaTemplate.send(topicName, key, message);

    kafkaResultFuture.whenComplete((result, throwable) -> {
      if (throwable != null) {
        LOG.error("Error while sending message {} to topic {}", message, topicName, throwable);
      } else {
        RecordMetadata metadata = result.getRecordMetadata();
        LOG.debug(
            "Received new metadata. Topic: {}; Partition: {}; Offset: {}; Timestamp: {}; at time:"
                + " {}",
            metadata.topic(), metadata.partition(), metadata.offset(), metadata.timestamp(),
            System.nanoTime());
      }
    });
  }

  @PreDestroy
  public void close() {
    if (kafkaTemplate != null) {
      LOG.info("Closing kafka producer!");
      kafkaTemplate.destroy();
    }
  }

}
