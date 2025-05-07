package com.microservices.demo.twitter.to.kafka.service.exception;

/**
 * Created by manitkumarpanda on 05/05/25
 */

public class TwitterToKafkaServiceException extends RuntimeException {

  public TwitterToKafkaServiceException() {
    super();
  }

  public TwitterToKafkaServiceException(String message) {
    super(message);
  }

  public TwitterToKafkaServiceException(String message, Throwable cause) {
    super(message, cause);
  }
}
