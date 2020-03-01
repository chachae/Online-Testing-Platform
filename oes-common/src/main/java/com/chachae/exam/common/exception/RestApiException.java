package com.chachae.exam.common.exception;

/**
 * @author chachae
 * @since 2020/3/1 18:00
 */
public class RestApiException extends RuntimeException {

  public RestApiException() {}

  public RestApiException(String message) {
    super(message);
  }

  public RestApiException(Throwable th) {
    super(th);
  }

  public RestApiException(String message, Throwable th) {
    super(message, th);
  }
}
