package com.microservices.demo.kafka.admin.exception;

/**
 * Created by manitkumarpanda on 07/05/25
 */

public class KafkaClientException extends RuntimeException {

  public KafkaClientException() {
  }

  public KafkaClientException(String message) {
    super(message);
  }

  public KafkaClientException(String message, Throwable cause) {
    super(message, cause);
  }
}
