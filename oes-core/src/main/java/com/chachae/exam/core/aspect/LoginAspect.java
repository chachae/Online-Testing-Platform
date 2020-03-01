package com.chachae.exam.core.aspect;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.chachae.exam.common.constant.SysConsts;
import com.chachae.exam.common.util.HttpContextUtil;
import com.chachae.exam.core.properties.Props;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author chachae
 * @since 2020/3/1 22:27
 */
public class LoginAspect {

  @Resource private Props props;

  /** 路径匹配器 */
  private AntPathMatcher pathMatcher = new AntPathMatcher();

  // 页面路径匹配
  private static final String ADMIN_PATTERN = "/admin/**";
  private static final String TEACHER_PATTERN = "/teacher/**";
  private static final String STU_PATTERN = "/student/**";

  /** 切点：带@Permission 注解的方法/接口 */
  @Pointcut("execution(* com.chachae.exam..*.*Controller.*(..))")
  public void loginPointcut() {}

  /**
   * 配置环绕通知,使用在方法logPointcut() 上注册的切入点
   *
   * @param joinPoint join point for advice
   */
  @Before("loginPointcut()")
  public void before(JoinPoint joinPoint) throws Throwable {

    // 当前请求路径
    String curPath = HttpContextUtil.getRequestUri();

    // 白名单匹配
    String[] anon = StrUtil.splitToArray(props.getSys().getAnonUrl(), StrUtil.C_COMMA);
    for (String e : anon) {
      if (pathMatcher.match(e, curPath)) {
        return;
      }
    }

    // 获取 response 对象
    HttpServletResponse response = HttpContextUtil.getHttpServletResponse();
    // 获取 session
    HttpSession session = HttpContextUtil.getSession();
    // session 判空
    if (ObjectUtil.isEmpty(session)) {
      response.sendRedirect("/login");
      HttpServletRequest req = HttpContextUtil.getHttpServletRequest();
    }

    // 过滤未认证请求
    // 管理员
    if (pathMatcher.match(ADMIN_PATTERN, curPath)) {
      if (ObjectUtil.isEmpty(session.getAttribute(SysConsts.Session.ADMIN))) {
        response.sendRedirect("/login");
      }
    }

    // 教师
    if (pathMatcher.match(TEACHER_PATTERN, curPath)) {
      if (ObjectUtil.isEmpty(session.getAttribute(SysConsts.Session.TEACHER))) {
        response.sendRedirect("/login");
      }
    }

    // 学生
    if (pathMatcher.match(STU_PATTERN, curPath)) {
      if (ObjectUtil.isEmpty(session.getAttribute(SysConsts.Session.STUDENT))) {
        response.sendRedirect("/login");
      }
    }
  }
}
