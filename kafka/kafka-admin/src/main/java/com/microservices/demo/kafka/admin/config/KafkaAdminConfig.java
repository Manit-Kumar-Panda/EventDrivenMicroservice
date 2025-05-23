package com.microservices.demo.kafka.admin.config;

import com.microservices.demo.config.KafkaConfigData;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.AdminClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;

import java.util.Map;

/**
 * Created by manitkumarpanda on 07/05/25
 */

@EnableRetry
@Configuration
public class KafkaAdminConfig {

  @Autowired
  private KafkaConfigData kafkaConfigData;

  @Bean
  public AdminClient adminClient() {
    return AdminClient.create(Map.of(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG,
        kafkaConfigData.getBootstrapServers()));
  }

}
