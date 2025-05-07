package com.microservices.demo.twitter.to.kafka.service.runner;

import twitter4j.TwitterException;

/**
 * Created by manitkumarpanda on 05/05/25
 */
public interface StreamRunner {

  public void start() throws TwitterException;

}
