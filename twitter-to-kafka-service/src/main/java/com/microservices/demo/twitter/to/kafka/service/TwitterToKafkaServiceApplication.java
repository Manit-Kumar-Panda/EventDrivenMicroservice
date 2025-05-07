package com.microservices.demo.twitter.to.kafka.service;

import com.microservices.demo.config.TwitterToKafkaServiceConfigData;
import com.microservices.demo.twitter.to.kafka.service.init.StreamInitializer;
import com.microservices.demo.twitter.to.kafka.service.runner.StreamRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.util.Arrays;


/**
 * Created by manitkumarpanda on 04/05/25
 */

@SpringBootApplication
@ComponentScan(basePackages = "com.microservices.demo")
public class TwitterToKafkaServiceApplication implements CommandLineRunner {

  @Autowired
  private TwitterToKafkaServiceConfigData twitterToKafkaServiceConfigData;

  private static final Logger LOG = LoggerFactory.getLogger(TwitterToKafkaServiceApplication.class);

  @Autowired
  private StreamRunner streamRunner;

  @Autowired
  private StreamInitializer streamInitializer;

  public static void main(String[] args) {
    SpringApplication.run(TwitterToKafkaServiceApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    LOG.info("App starts...");
    LOG.info(Arrays.toString(
        twitterToKafkaServiceConfigData.getTwitterKeywords().toArray(new String[] {})));
    streamInitializer.init();
    streamRunner.start();
  }
}
