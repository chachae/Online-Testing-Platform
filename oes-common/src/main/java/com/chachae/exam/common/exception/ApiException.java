package com.chachae.exam.common.exception;

/**
 * @author chachae
 * @since 2020/3/1 18:00
 */
public class ApiException extends RuntimeException {

  public ApiException() {}

  public ApiException(String message) {
    super(message);
  }

  public ApiException(Throwable th) {
    super(th);
  }

  public ApiException(String message, Throwable th) {
    super(message, th);
  }
}
