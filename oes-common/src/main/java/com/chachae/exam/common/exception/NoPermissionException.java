package com.chachae.exam.common.exception;

/**
 * 无权限访问异常
 *
 * @author chachae
 * @since 2020/3/1 17:56
 */
public class NoPermissionException extends RuntimeException {

  public NoPermissionException() {}

  public NoPermissionException(String message) {
    super(message);
  }

  public NoPermissionException(Throwable th) {
    super(th);
  }

  public NoPermissionException(String message, Throwable th) {
    super(message, th);
  }
}
