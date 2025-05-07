package com.microservices.demo.twitter.to.kafka.service.init.impl;

import com.microservices.demo.config.KafkaConfigData;
import com.microservices.demo.kafka.admin.client.KafkaAdminClient;
import com.microservices.demo.twitter.to.kafka.service.init.StreamInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by manitkumarpanda on 07/05/25
 */

@Component
public class KafkaStreamInitializer implements StreamInitializer {

  private static final Logger LOG = LoggerFactory.getLogger(KafkaStreamInitializer.class);

  @Autowired
  private KafkaConfigData kafkaConfigData;

  @Autowired
  private KafkaAdminClient kafkaAdminClient;

  @Override
  public void init() {
    kafkaAdminClient.createTopics();
    kafkaAdminClient.checkSchemaRegistry();
    LOG.info("Topic with names:{} is ready for operation!!",
        kafkaConfigData.getTopicNamesToCreate().toArray());
  }


}
