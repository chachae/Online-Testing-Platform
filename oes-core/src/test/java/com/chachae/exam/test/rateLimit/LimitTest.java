package com.chachae.exam.test.rateLimit;

import com.google.common.util.concurrent.RateLimiter;
import org.junit.jupiter.api.Test;

/**
 * @author chachae
 * @since 2020/3/11 15:18
 */
public class LimitTest {

  private RateLimiter limiter = RateLimiter.create(1);

  @Test
  public void test() {

    for (int i = 0; i < 5; i++) {
      if (limiter.tryAcquire()) {
        System.out.println("success");
      } else {
        System.out.println("fail");
      }
    }
  }
}
