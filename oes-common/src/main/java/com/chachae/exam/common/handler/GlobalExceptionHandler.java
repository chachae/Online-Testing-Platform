package com.chachae.exam.common.handler;

import com.chachae.exam.common.base.R;
import com.chachae.exam.common.exception.LimitAccessException;
import com.chachae.exam.common.exception.NoPermissionException;
import com.chachae.exam.common.exception.ServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import java.util.Objects;

/**
 * @author chachae
 * @since 2020/2/27 22:40
 */
@ControllerAdvice
public class GlobalExceptionHandler {

  @ResponseBody
  @ExceptionHandler(NoPermissionException.class)
  public R handleNoPermissionException(NoPermissionException e) {
    return R.error(e.getMessage());
  }

  @ResponseBody
  @ExceptionHandler(LimitAccessException.class)
  public R handleLimitAccessException(LimitAccessException e) {
    return R.error(e.getMessage());
  }


  @ResponseBody
  @ExceptionHandler(ServiceException.class)
  public R handleServiceException(ServiceException e) {
    return R.error(e.getMessage());
  }

  /** 处理所有接口数据验证异常 */
  @ResponseBody
  @ExceptionHandler(BindException.class)
  public R handleBindException(BindException e) {
    String[] str =
        (Objects.requireNonNull(e.getBindingResult().getAllErrors().get(0).getCodes()))
            [1].split("\\.");
    String message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
    String msg = "不能为空";
    if (msg.equals(message)) {
      message = str[1] + ":" + message;
    }
    return R.error(message);
  }
}
