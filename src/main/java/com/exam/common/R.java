package com.exam.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ajax 请求的返回结果类
 *
 * @author yzn
 * @date 2019/12/31
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class R {

  // 默认的成功信息
  private static final String STATE_SUCCESS = "success";
  // 磨人的失败信息
  private static final String STATE_ERROR = "error";

  private String message;
  private String state;
  private Object data;

  /**
   * 成功回调信息
   *
   * @return 成功的回调数据
   */
  public static R success() {
    R r = new R();
    r.setState(STATE_SUCCESS);
    return r;
  }

  /**
   * 失败的回调信息
   *
   * @param message 消息（可以放异常的Message）
   * @return 失败的回调信息
   */
  public static R error(String message) {
    R r = new R();
    r.setMessage(message);
    r.setData(STATE_ERROR);
    return r;
  }

  /**
   * 带数据的成功回调信息
   *
   * @param data 数据
   * @return 成功回调信息
   */
  public static R successWithData(Object data) {
    R r = new R();
    r.setState(STATE_SUCCESS);
    r.setData(data);
    return r;
  }
}
