package com.chachae.exam.common.exception;

/**
 * 统一业务层异常
 *
 * @author chachae
 * @date 2019/12/29
 */
public class ServiceException extends RuntimeException {

  public ServiceException(String message) {
    super(message);
  }
}
