package com.exam.util;

import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.google.common.collect.Maps;
import com.google.common.net.HttpHeaders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.Objects;

/**
 * @author yzn
 * @since 2019/12/21 11:02
 */
public class HttpContextUtil {

  private HttpContextUtil() {}

  /**
   * 获取 HttpServletRequest 对象
   *
   * @return /
   */
  public static HttpServletRequest getHttpServletRequest() {
    return ((ServletRequestAttributes)
            Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))
        .getRequest();
  }

  /**
   * 获取Session 对象
   *
   * @return
   */
  public static HttpSession getSession() {
    return getHttpServletRequest().getSession();
  }

  /**
   * 获取头部信息
   *
   * @param header /
   * @return /
   */
  public static String getHeader(String header) {
    return getHttpServletRequest().getHeader(header);
  }

  /** 解析 User-Agent信息 */
  public static UserAgent getUserAgent() {
    String userAgent = getHeader(HttpHeaders.USER_AGENT);
    return UserAgentUtil.parse(userAgent);
  }

  /** 获取请求接口 */
  public static String getRequestUri() {
    return getHttpServletRequest().getRequestURI();
  }

  /**
   * 获取请求参数
   *
   * @return /
   */
  public static Map<String, String[]> getParams() {
    return getHttpServletRequest().getParameterMap();
  }
}
