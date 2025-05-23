package com.microservices.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Created by manitkumarpanda on 04/05/25
 */

@Data
@Configuration
@ConfigurationProperties(prefix = "twitter-to-kafka-service")
public class TwitterToKafkaServiceConfigData {

  private List<String> twitterKeywords;
  private Boolean enableMockTweets;
  private Integer mockMinTweetLength;
  private Integer mockMaxTweetLength;
  private Long mockSleepMs;

}
