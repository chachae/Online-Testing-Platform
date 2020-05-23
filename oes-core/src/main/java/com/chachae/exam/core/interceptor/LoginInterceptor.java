package com.chachae.exam.core.interceptor;

import cn.hutool.core.util.StrUtil;
import com.chachae.exam.common.constant.SysConsts.Session;
import com.chachae.exam.core.properties.Props;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * 登录拦截器
 *
 * @author chachae
 * @since 2020/3/1 23:52
 */
@Component
@RequiredArgsConstructor
public class LoginInterceptor extends HandlerInterceptorAdapter {

  private final Props props;

  /**
   * 路径匹配器
   */
  private static final AntPathMatcher MATCHER = new AntPathMatcher();

  /**
   * 角色页面路径匹配
   */
  private static final String[] ROLE_PATTERNS = {"/admin/**", "/teacher/**", "/student/**"};

  /**
   * REST API 匹配
   */
  private static final String REST_PATTERN = "/api/**";

  @Override
  public boolean preHandle(HttpServletRequest request, @NonNull HttpServletResponse response,
      @NonNull Object handler)
      throws Exception {

    // 当前请求路径
    String curPath = request.getRequestURI();

    // 白名单匹配
    String[] anons = StrUtil.splitToArray(props.getSys().getAnonUrl(), ',');
    for (String anon : anons) {
      // 成功匹配表名单则放行
      if (MATCHER.match(anon, curPath)) {
        return true;
      }
    }

    // 获取 session
    HttpSession session = request.getSession();

    // session 判空
    if (session == null) {
      response.sendRedirect(props.getSys().getLoginUrl());
      return false;
    }

    // rest 接口一律放行，由 AOP 授权器决定请求是否合法
    if (MATCHER.match(REST_PATTERN, curPath)) {
      return true;
    }

    // 管理员
    if (MATCHER.match(ROLE_PATTERNS[0], curPath)
        && session.getAttribute(Session.ADMIN) != null) {
      return true;
    }

    // 教师
    if (MATCHER.match(ROLE_PATTERNS[1], curPath)
        && session.getAttribute(Session.TEACHER) != null) {
      return true;
    }

    // 学生
    if (MATCHER.match(ROLE_PATTERNS[2], curPath)
        && session.getAttribute(Session.STUDENT) != null) {
      return true;
    }

    // 未知请求一律过滤
    response.sendRedirect(props.getSys().getLoginUrl());
    return false;
  }
}
