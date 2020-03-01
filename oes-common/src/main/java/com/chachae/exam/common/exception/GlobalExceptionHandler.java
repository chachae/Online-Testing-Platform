package com.chachae.exam.common.exception;

import com.chachae.exam.common.base.R;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author chachae
 * @since 2020/2/27 22:40
 */
@ControllerAdvice
public class GlobalExceptionHandler {

  @ResponseStatus(HttpStatus.OK)
  @ExceptionHandler(ApiException.class)
  public ModelAndView handleServiceException(ApiException e) {
    String message = e.getMessage();
    ModelAndView mv = new ModelAndView();
    mv.addObject("message", message);
    mv.setViewName("/page/403");
    return mv;
  }

  @ResponseBody
  @ResponseStatus(HttpStatus.OK)
  @ExceptionHandler(RestApiException.class)
  public R handleServiceException(RestApiException e) {
    return R.error(e.getMessage());
  }

  @ResponseBody
  @ResponseStatus(HttpStatus.OK)
  @ExceptionHandler(ServiceException.class)
  public R handleServiceException(ServiceException e) {
    return R.error(e.getMessage());
  }
}
