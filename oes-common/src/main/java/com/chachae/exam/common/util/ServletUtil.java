package com.chachae.exam.common.util;

import cn.hutool.core.util.StrUtil;
import com.google.common.net.HttpHeaders;
import java.nio.charset.StandardCharsets;
import javax.servlet.http.HttpServletRequest;

/**
 * ServletUtil 拓展
 *
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
    HttpServletRequest req = HttpUtil.getHttpServletRequest();
    return StrUtil.isNotBlank(
        ServletUtil.getHeader(req, HttpHeaders.X_REQUESTED_WITH, StandardCharsets.UTF_8))
        && StrUtil.equalsIgnoreCase(
        ServletUtil.getHeader(req, HttpHeaders.X_REQUESTED_WITH, StandardCharsets.UTF_8),
        "XMLHttpRequest");
  }
}
