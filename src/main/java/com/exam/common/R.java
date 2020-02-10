package com.exam.common;

/**
 * ajax 请求的返回结果类
 *
 * @author yzn
 * @date 2019/12/31
 */
public class R {

  private static final String STATE_SUCCESS = "success";
  private static final String STATE_ERROR = "error";

  private String message;
  private String state;
  private Object data;

  public static R success() {
    R r = new R();
    r.setState(STATE_SUCCESS);
    return r;
  }

  public static R error(String message) {
    R r = new R();
    r.setData(STATE_ERROR);
    return r;
  }

  public static R successWithData(Object data) {
    R r = new R();
    r.setState(STATE_SUCCESS);
    r.setData(data);
    return r;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public Object getData() {
    return data;
  }

  public void setData(Object data) {
    this.data = data;
  }
}
