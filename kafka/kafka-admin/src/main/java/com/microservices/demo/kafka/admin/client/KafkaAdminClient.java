package com.microservices.demo.kafka.admin.client;

import com.microservices.demo.config.KafkaConfigData;
import com.microservices.demo.config.RetryConfigData;
import com.microservices.demo.kafka.admin.exception.KafkaClientException;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicListing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by manitkumarpanda on 07/05/25
 */

@Component
public class KafkaAdminClient {

  public static final Logger LOG = LoggerFactory.getLogger(KafkaAdminClient.class);

  @Autowired
  private KafkaConfigData kafkaConfigData;

  @Autowired
  private RetryConfigData retryConfigData;

  @Autowired
  private RetryTemplate retryTemplate;

  @Autowired
  private AdminClient adminClient;

  @Autowired
  private WebClient webClient;

  public void createTopics() {
    CreateTopicsResult createTopicsResult;
    try {
      createTopicsResult = retryTemplate.execute(this::doCreateTopics);
    } catch (Throwable throwable) {
      throw new KafkaClientException(
          "Reached maximum number of retry for creating kafka topic(s)!");
    }
    checkTopicsCreated();
  }

  private CreateTopicsResult doCreateTopics(RetryContext retryContext) {
    List<String> topicNames = kafkaConfigData.getTopicNamesToCreate();
    LOG.info("Creating {} topic(s), attempt: {}", topicNames.size(), retryContext.getRetryCount());
    List<NewTopic> kafkaTopics = topicNames.stream().map(
        topic -> new NewTopic(topic.trim(), kafkaConfigData.getNumOfPartitions(),
            kafkaConfigData.getReplicationFactor())).collect(Collectors.toList());
    return adminClient.createTopics(kafkaTopics);
  }


  public void checkSchemaRegistry() {
    int retryCount = 1;
    Integer maxRetry = retryConfigData.getMaxAttempts();
    int multiplier = retryConfigData.getMultiplier().intValue();
    Long sleepTimeMs = retryConfigData.getSleepTimeMs();
    while (!getSchemaRegistryStatus().is2xxSuccessful()) {
      checkMaxRetry(retryCount++, maxRetry);
      sleep(sleepTimeMs);
      sleepTimeMs *= multiplier;
    }
  }

  private HttpStatusCode getSchemaRegistryStatus() {
    try {
      return webClient.method(HttpMethod.GET).uri(kafkaConfigData.getSchemaRegistryUrl()).exchange()
          .map(ClientResponse::statusCode).block();
    } catch (Exception e) {
      return HttpStatus.SERVICE_UNAVAILABLE;
    }
  }

  public void checkTopicsCreated() {
    Collection<TopicListing> topics = getTopics();
    int retryCount = 1;
    Integer maxRetry = retryConfigData.getMaxAttempts();
    int multiplier = retryConfigData.getMultiplier().intValue();
    Long sleepTimeMs = retryConfigData.getSleepTimeMs();
    for (String topic : kafkaConfigData.getTopicNamesToCreate()) {
      while (!isTopicCreated(topics, topic)) {
        checkMaxRetry(retryCount++, maxRetry);
        sleep(sleepTimeMs);
        sleepTimeMs *= multiplier;
        topics = getTopics();
      }
    }
  }

  private boolean isTopicCreated(Collection<TopicListing> topics, String topicName) {
    if (topics == null) {
      return false;
    }
    return topics.stream().anyMatch(topic -> topic.name().equals(topicName));
  }

  private void sleep(Long sleepTimeMs) {
    try {
      Thread.sleep(sleepTimeMs);
    } catch (InterruptedException e) {
      throw new KafkaClientException("Error while sleeping for waiting new created topics!!");
    }
  }

  private void checkMaxRetry(int retry, Integer maxRetry) {
    if (retry > maxRetry) {
      throw new KafkaClientException("Reached max number of retry for reading kafka topic(s)!");
    }
  }

  private Collection<TopicListing> getTopics() {
    Collection<TopicListing> topics;
    try {
      topics = retryTemplate.execute(this::doGetTopics);
    } catch (RuntimeException e) {
      throw new KafkaClientException("Reached max number of retry for reading kafka topics!!");
    }
    return topics;
  }

  private Collection<TopicListing> doGetTopics(RetryContext retryContext) {
    Collection<TopicListing> topics;
    try {
      topics = adminClient.listTopics().listings().get();
      if (topics != null) {
        topics.forEach(topic -> LOG.debug("Topic with name:{}", topic.name()));
      }
    } catch (Exception e) {
      throw new KafkaClientException("Something went wrong in doGetTopics");
    }
    return topics;
  }

}
