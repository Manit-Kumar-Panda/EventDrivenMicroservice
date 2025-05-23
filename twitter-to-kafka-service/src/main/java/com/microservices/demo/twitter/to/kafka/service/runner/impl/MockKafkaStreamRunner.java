package com.microservices.demo.twitter.to.kafka.service.runner.impl;

import com.microservices.demo.config.TwitterToKafkaServiceConfigData;
import com.microservices.demo.twitter.to.kafka.service.exception.TwitterToKafkaServiceException;
import com.microservices.demo.twitter.to.kafka.service.listener.TwitterKafkaStatusListener;
import com.microservices.demo.twitter.to.kafka.service.runner.StreamRunner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by manitkumarpanda on 05/05/25
 */

@Slf4j
@Component
@ConditionalOnProperty(name = "twitter-to-kafka-service.enable-mock-tweets", havingValue = "true",
    matchIfMissing = true)
public class MockKafkaStreamRunner implements StreamRunner {

  @Autowired
  private TwitterToKafkaServiceConfigData twitterToKafkaServiceConfigData;

  @Autowired
  private TwitterKafkaStatusListener twitterKafkaStatusListener;

  public static final Random RANDOM = new Random();

  public static final String[] WORDS =
      new String[] {"Lorem", "ipsum", "dolor", "sit", "amet", "consectetur", "adipiscing", "elit",
          "Sed", "do", "eiusmod", "tempor", "incididunt", "ut", "labore", "et", "dolore", "magna",
          "aliqua", "Ut", "enim", "ad", "minim", "veniam", "quis", "nostrud", "exercitation" };

  private static final String tweetAsRawJson =
      "{" + "\"created_at\":\"{0}\"," + "\"id\":\"{1}\"," + "\"text\":\"{2}\","
          + "\"user\":{\"id\":\"{3}\"}" + "}";

  private static final String TWITTER_STATUS_DATE_FORMAT = "EEE MMM dd HH:mm:ss zzz yyyy";

  @Override
  public void start() throws TwitterException {
    String[] keywords = twitterToKafkaServiceConfigData.getTwitterKeywords().toArray(new String[0]);
    int minTweetLength = twitterToKafkaServiceConfigData.getMockMinTweetLength();
    int maxTweetLength = twitterToKafkaServiceConfigData.getMockMaxTweetLength();
    long sleepTimeMs = twitterToKafkaServiceConfigData.getMockSleepMs();
    log.info("Starting mock filtering twitter streams for keywords: {}", Arrays.toString(keywords));
    simulateTwitterStream(keywords, minTweetLength, maxTweetLength, sleepTimeMs);
  }

  private void simulateTwitterStream(String[] keywords, int minTweetLength, int maxTweetLength,
      long sleepTimeMs) {
    Executors.newSingleThreadExecutor().submit(() -> {
      try {
        while (true) {
          String formattedTweetAsRawJson =
              getFormattedTweet(keywords, minTweetLength, maxTweetLength);
          Status status = TwitterObjectFactory.createStatus(formattedTweetAsRawJson);
          twitterKafkaStatusListener.onStatus(status);
          sleep(sleepTimeMs);
        }
      } catch (TwitterException e) {
        log.error("Error creating twitter status!", e);
      }
    });
  }

  private void sleep(long sleepTimeMs) {
    try {
      Thread.sleep(sleepTimeMs);
    } catch (InterruptedException e) {
      throw new TwitterToKafkaServiceException(
          "Error while sleeping for waiting new status to create !!!");
    }
  }

  private String getFormattedTweet(String[] keywords, int minTweetLength, int maxTweetLength) {
    String[] params = new String[] {ZonedDateTime.now().format(
        DateTimeFormatter.ofPattern(TWITTER_STATUS_DATE_FORMAT, Locale.ENGLISH)),
        String.valueOf(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE)),
        getRandomTweetContent(keywords, minTweetLength, maxTweetLength),
        String.valueOf(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE))};
    return formatTweetAsJsonWithParams(params);
  }

  private String formatTweetAsJsonWithParams(String[] params) {
    String tweet = tweetAsRawJson;

    for (int i = 0; i < params.length; i++) {
      tweet = tweet.replace("{" + i + "}", params[i]);
    }
    return tweet;
  }

  private String getRandomTweetContent(String[] keywords, int minTweetLength, int maxTweetLength) {
    StringBuilder tweet = new StringBuilder();
    int tweetLength = RANDOM.nextInt(maxTweetLength - minTweetLength + 1) + minTweetLength;
    return constructRandomTweet(keywords, tweet, tweetLength);
  }

  private String constructRandomTweet(String[] keywords, StringBuilder tweet, int tweetLength) {
    for (int i = 0; i < tweetLength; i++) {
      tweet.append(WORDS[RANDOM.nextInt(WORDS.length)]).append(" ");
      if (i == tweetLength / 2) {
        tweet.append(keywords[RANDOM.nextInt(keywords.length)]).append(" ");
      }
    }
    return tweet.toString().trim();
  }


}
