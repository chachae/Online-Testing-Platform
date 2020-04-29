package com.chachae.exam.common.util;

import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.google.common.net.HttpHeaders;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * HttpUtil 拓展
 *
 * @author chachae
 * @since 2019/12/21 11:02
 */
public class HttpUtil extends cn.hutool.http.HttpUtil {

  private HttpUtil() {
  }

  private static final String UNKNOWN = "unknown";


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
   * 获取 HttpServletRequest 对象
   *
   * @return /
   */
  public static HttpServletResponse getHttpServletResponse() {
    return ((ServletRequestAttributes)
        Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))
        .getResponse();
  }

  /**
   * 获取Session 对象
   *
   * @return HttpSession
   */
  public static HttpSession getSession() {
    return getHttpServletRequest().getSession();
  }

  /**
   * 获取Session 对象信息
   *
   * @return obj
   */
  public static Object getAttribute(String attr) {
    return getHttpServletRequest().getSession().getAttribute(attr);
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

  /**
   * 解析 User-Agent信息
   */
  public static UserAgent getUserAgent() {
    String userAgent = getHeader(HttpHeaders.USER_AGENT);
    return UserAgentUtil.parse(userAgent);
  }

  public static String getParam(String key) {
    return getHttpServletRequest().getParameter(key);
  }

  /**
   * 获取 IP地址 使用 Nginx等反向代理软件， 则不能通过 request.getRemoteAddr()获取 IP地址 如果使用了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP地址，
   * X-Forwarded-For中第一个非 unknown的有效IP字符串，则为真实IP地址
   */
  public static String getIpAddr(HttpServletRequest request) {
    String ip = request.getHeader("x-forwarded-for");
    if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
      ip = request.getHeader("Proxy-Client-IP");
    }
    if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
      ip = request.getHeader("WL-Proxy-Client-IP");
    }
    if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
      ip = request.getRemoteAddr();
    }
    return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
  }
}
