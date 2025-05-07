package com.microservices.demo.twitter.to.kafka.service.transformer;

import com.microservices.demo.kafka.avro.model.TwitterAvroModel;
import org.springframework.stereotype.Component;
import twitter4j.Status;

/**
 * Created by manitkumarpanda on 07/05/25
 */

@Component
public class TwitterStatusToAvroTransformer {

  public TwitterAvroModel getTwitterAvroModelForStatus(Status status) {
    return TwitterAvroModel.newBuilder().setId(status.getId()).setUserId(status.getUser().getId())
        .setText(status.getText()).setCreatedAt(status.getCreatedAt().getTime()).build();
  }

}
