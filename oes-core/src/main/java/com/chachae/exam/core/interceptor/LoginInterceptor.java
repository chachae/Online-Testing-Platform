package com.chachae.exam.core.interceptor;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.chachae.exam.common.constant.SysConsts;
import com.chachae.exam.common.entity.Admin;
import com.chachae.exam.common.entity.Student;
import com.chachae.exam.common.entity.Teacher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 拦截器
 *
 * @author yzn
 * @date 2020/1/16
 */
@Component
public class LoginInterceptor extends HandlerInterceptorAdapter {

  /** 登录接口 */
  @Value("${anon.uri}")
  public String anonUri;

  /** 静态资源匹配接口 */
  @Value("${match.uri}")
  public String matchUri;

  /** 公共访问接口 */
  @Value("${common.uri}")
  public String commonUri;

  // 不同身份的接口访问前缀
  private static final String ADMIN_PREFIX = "/admin";
  private static final String STUDENT_PREFIX = "/student";
  private static final String TEACHER_PREFIX = "/teacher";

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {

    // 获取当前访问的路径
    String uri = request.getRequestURI();
    String[] whiteList = StrUtil.splitToArray(anonUri, StrUtil.C_COMMA);
    String[] matchList = StrUtil.splitToArray(matchUri, StrUtil.C_COMMA);
    String[] commonList = StrUtil.splitToArray(commonUri, StrUtil.C_COMMA);

    // 处理白名单
    for (String e : whiteList) {
      if (e.equals(uri)) {
        return true;
      }
    }

    // 处理静态资源
    for (String e : matchList) {
      if (uri.startsWith(e)) {
        return true;
      }
    }

    // 处理 session 访问
    HttpSession session = request.getSession();
    // 获取管理员的session
    Admin admin = (Admin) session.getAttribute(SysConsts.SESSION.ADMIN);
    // 获取教师的 session
    Teacher teacher = (Teacher) session.getAttribute(SysConsts.SESSION.TEACHER);
    // 获取学生的session
    Student student = (Student) session.getAttribute(SysConsts.SESSION.STUDENT);

    // 判断 session 情况
    boolean exitSessionRes = (admin != null || teacher != null || student != null);
    boolean adminRes = ObjectUtil.isNotEmpty(admin) && uri.startsWith(ADMIN_PREFIX);
    boolean teacherRes = ObjectUtil.isNotEmpty(teacher) && uri.startsWith(TEACHER_PREFIX);
    boolean studentRes = ObjectUtil.isNotEmpty(student) && uri.startsWith(STUDENT_PREFIX);
    if (adminRes || teacherRes || studentRes) {
      return true;
    }

    // 处理公共接口访问
    for (String e : commonList) {
      if (uri.startsWith(e) && exitSessionRes) {
        return true;
      }
    }

    // 如果以上判断全部匹配不到，说明是非法访问，直接使用 response 对象重定向到登录页面
    response.sendRedirect("/");
    return false;
  }
}
