package com.microservices.demo.common.config;

import com.microservices.demo.config.RetryConfigData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

/**
 * Created by manitkumarpanda on 06/05/25
 */

@Configuration
public class RetryConfig {

  @Autowired
  private RetryConfigData retryConfigData;

  @Bean
  public RetryTemplate retryTemplate() {
    RetryTemplate retryTemplate = new RetryTemplate();

    ExponentialBackOffPolicy exponentialBackOffPolicy = new ExponentialBackOffPolicy();
    exponentialBackOffPolicy.setInitialInterval(retryConfigData.getInitialIntervalMs());
    exponentialBackOffPolicy.setMaxInterval(retryConfigData.getMaxIntervalMs());
    exponentialBackOffPolicy.setMultiplier(retryConfigData.getMultiplier());

    retryTemplate.setBackOffPolicy(exponentialBackOffPolicy);

    SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy();
    simpleRetryPolicy.setMaxAttempts(retryConfigData.getMaxAttempts());

    retryTemplate.setRetryPolicy(simpleRetryPolicy);

    return retryTemplate;
  }

}
