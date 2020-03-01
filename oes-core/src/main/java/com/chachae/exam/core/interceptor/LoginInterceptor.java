package com.chachae.exam.core.interceptor;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.chachae.exam.common.constant.SysConsts;
import com.chachae.exam.common.util.HttpContextUtil;
import com.chachae.exam.core.properties.Props;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author chachae
 * @since 2020/3/1 23:52
 */
@Component
public class LoginInterceptor extends HandlerInterceptorAdapter {

  @Resource private Props props;

  /** 路径匹配器 */
  private AntPathMatcher pathMatcher = new AntPathMatcher();

  // 页面路径匹配
  private static final String ADMIN_PATTERN = "/admin/**";
  private static final String TEACHER_PATTERN = "/teacher/**";
  private static final String STU_PATTERN = "/student/**";

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {

    // 当前请求路径
    String curPath = HttpContextUtil.getRequestUri();

    // 白名单匹配
    String[] anon = StrUtil.splitToArray(props.getSys().getAnonUrl(), StrUtil.C_COMMA);
    for (String e : anon) {
      if (pathMatcher.match(e, curPath)) {
        return true;
      }
    }

    // 获取 session
    HttpSession session = HttpContextUtil.getSession();
    // session 判空
    if (ObjectUtil.isEmpty(session)) {
      response.sendRedirect("/login");
      return false;
    }

    // 过滤未认证请求
    // 管理员
    if (pathMatcher.match(ADMIN_PATTERN, curPath)) {
      if (ObjectUtil.isEmpty(session.getAttribute(SysConsts.Session.ADMIN))) {
        response.sendRedirect("/login");
        return false;
      }
    }

    // 教师
    if (pathMatcher.match(TEACHER_PATTERN, curPath)) {
      if (ObjectUtil.isEmpty(session.getAttribute(SysConsts.Session.TEACHER))) {
        response.sendRedirect("/login");
        return false;
      }
    }

    // 学生
    if (pathMatcher.match(STU_PATTERN, curPath)) {
      if (ObjectUtil.isEmpty(session.getAttribute(SysConsts.Session.STUDENT))) {
        response.sendRedirect("/login");
        return false;
      }
    }

    return true;
  }
}
