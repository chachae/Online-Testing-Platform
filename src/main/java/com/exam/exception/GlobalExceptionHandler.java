package com.exam.exception;

import com.exam.common.R;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author chachae
 * @since 2020/2/27 22:40
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ResponseStatus(HttpStatus.OK)
  @ExceptionHandler(ServiceException.class)
  public R handleServiceException(ServiceException e) {
    return R.error(e.getMessage());
  }
}
