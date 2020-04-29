package com.chachae.exam.core.interceptor;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.chachae.exam.common.constant.SysConsts;
import com.chachae.exam.core.properties.Props;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
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
public class LoginInterceptor extends HandlerInterceptorAdapter {

  @Resource
  private Props props;

  /**
   * 路径匹配器
   */
  private final AntPathMatcher pathMatcher = new AntPathMatcher();

  // 页面路径匹配
  private static final String ADMIN_PATTERN = "/admin/**";
  private static final String TEACHER_PATTERN = "/teacher/**";
  private static final String STU_PATTERN = "/student/**";
  private static final String REST_PATTERN = "/api/**";

  @Override
  public boolean preHandle(HttpServletRequest request, @NonNull HttpServletResponse response,
      @NonNull Object handler)
      throws Exception {

    // 当前请求路径
    String curPath = request.getRequestURI();

    // 白名单匹配
    String[] anon = StrUtil.splitToArray(props.getSys().getAnonUrl(), StrUtil.C_COMMA);
    for (String e : anon) {
      // 成功匹配表名单则放行
      if (pathMatcher.match(e, curPath)) {
        return true;
      }
    }

    // 获取 session
    HttpSession session = request.getSession();

    // session 判空
    if (ObjectUtil.isEmpty(session)) {
      response.sendRedirect(props.getSys().getLoginUrl());
      return false;
    }

    // rest 接口一律放行，由 AOP 授权器决定请求是否合法
    if (pathMatcher.match(REST_PATTERN, curPath)) {
      if (ObjectUtil.isNotEmpty(session)) {
        return true;
      }
    }

    // 管理员
    if (pathMatcher.match(ADMIN_PATTERN, curPath)) {
      if (ObjectUtil.isNotEmpty(session.getAttribute(SysConsts.Session.ADMIN))) {
        return true;
      }
    }

    // 教师
    if (pathMatcher.match(TEACHER_PATTERN, curPath)) {
      if (ObjectUtil.isNotEmpty(session.getAttribute(SysConsts.Session.TEACHER))) {
        return true;
      }
    }

    // 学生
    if (pathMatcher.match(STU_PATTERN, curPath)) {
      if (ObjectUtil.isNotEmpty(session.getAttribute(SysConsts.Session.STUDENT))) {
        return true;
      }
    }

    // 未知请求一律过滤
    response.sendRedirect(props.getSys().getLoginUrl());
    return false;
  }
}
