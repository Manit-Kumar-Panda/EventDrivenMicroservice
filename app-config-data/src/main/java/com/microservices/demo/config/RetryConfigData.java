package com.microservices.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created by manitkumarpanda on 06/05/25
 */

@Data
@Configuration
@ConfigurationProperties(prefix = "retry-config")
public class RetryConfigData {

  private Long initialIntervalMs;
  private Long maxIntervalMs;
  private Double multiplier;
  private Integer maxAttempts;
  private Long sleepTimeMs;

}
