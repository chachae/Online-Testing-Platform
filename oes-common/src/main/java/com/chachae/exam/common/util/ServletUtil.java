package com.chachae.exam.common.util;

import cn.hutool.core.util.StrUtil;
import com.google.common.base.Charsets;
import com.google.common.net.HttpHeaders;

import javax.servlet.http.HttpServletRequest;

/**
 * @author chachae
 * @since 2020/3/9 22:46
 */
public class ServletUtil extends cn.hutool.extra.servlet.ServletUtil {

  /**
   * 判斷请求是否为 Ajax 请求
   *
   * @return boolean
   */
  public static boolean isAjax() {
    HttpServletRequest req = HttpContextUtil.getHttpServletRequest();
    return StrUtil.isNotBlank(
            ServletUtil.getHeader(req, HttpHeaders.X_REQUESTED_WITH, Charsets.UTF_8.name()))
        && StrUtil.equalsIgnoreCase(
            ServletUtil.getHeader(req, HttpHeaders.X_REQUESTED_WITH, Charsets.UTF_8.name()),
            "XMLHttpRequest");
  }
}
