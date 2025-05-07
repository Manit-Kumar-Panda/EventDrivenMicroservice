package com.microservices.demo.twitter.to.kafka.service.listener;

import com.microservices.demo.config.KafkaConfigData;
import com.microservices.demo.kafka.avro.model.TwitterAvroModel;
import com.microservices.demo.kafka.producer.config.service.KafkaProducer;
import com.microservices.demo.twitter.to.kafka.service.transformer.TwitterStatusToAvroTransformer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import twitter4j.Status;
import twitter4j.StatusAdapter;

/**
 * Created by manitkumarpanda on 05/05/25
 */

@Slf4j
@Component
public class TwitterKafkaStatusListener extends StatusAdapter {

  @Autowired
  private KafkaConfigData kafkaConfigData;

  @Autowired
  private KafkaProducer<Long, TwitterAvroModel> kafkaProducer;

  @Autowired
  private TwitterStatusToAvroTransformer twitterStatusToAvroTransformer;

  @Override
  public void onStatus(Status status) {
    log.info("Twitter status with text {} sending to kafka topic:{} ", status.getText(),
        kafkaConfigData.getTopicName());
    TwitterAvroModel twitterAvroModel =
        twitterStatusToAvroTransformer.getTwitterAvroModelForStatus(status);
    kafkaProducer.send(kafkaConfigData.getTopicName(), twitterAvroModel.getUserId(),
        twitterAvroModel);
  }
}
