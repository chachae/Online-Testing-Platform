package com.exam.exception;

/**
 * 统一业务层异常
 *
 * @author yzn
 * @date 2019/12/29
 */
public class ServiceException extends RuntimeException {

  public ServiceException() {}

  public ServiceException(String message) {
    super(message);
  }

  public ServiceException(Throwable th) {
    super(th);
  }

  public ServiceException(String message, Throwable th) {
    super(message, th);
  }
}
