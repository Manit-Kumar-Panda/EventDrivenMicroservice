package com.microservices.demo.config.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * Created by manitkumarpanda on 08/05/25
 */

@EnableConfigServer
@SpringBootApplication
public class ConfigServer {

  public static void main(String[] args) {
    SpringApplication.run(ConfigServer.class, args);
  }

}
