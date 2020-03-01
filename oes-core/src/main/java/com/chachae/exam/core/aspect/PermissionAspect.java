package com.chachae.exam.core.aspect;

import com.chachae.exam.common.constant.SysConsts;
import com.chachae.exam.common.exception.ApiException;
import com.chachae.exam.common.exception.RestApiException;
import com.chachae.exam.common.util.HttpContextUtil;
import com.chachae.exam.core.annotation.Permissions;
import com.chachae.exam.service.PermissionService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * @author chachae
 * @since 2020/3/1 17:36
 */
@Slf4j
@Aspect
@Order(1)
@Component
public class PermissionAspect {

  @Resource private PermissionService permissionService;

  /** 切点：带@Permission 注解的方法/接口 */
  @Pointcut("@annotation(com.chachae.exam.core.annotation.Permissions)")
  public void permissionPointCut() {}

  /**
   * 配置环绕通知,使用在方法logPointcut() 上注册的切入点
   *
   * @param joinPoint join point for advice
   */
  @Around("permissionPointCut()")
  public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
    // 获取用户角色信息
    int roleId = (int) HttpContextUtil.getAttribute(SysConsts.Session.ROLE_ID);
    Set<String> permissions = this.permissionService.selectExpressionByRoleId(roleId);

    // 强转MethodSignature
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Method method = signature.getMethod();

    // 获取注解信息
    Permissions annotation = method.getAnnotation(Permissions.class);
    String permission = annotation.value();
    // 不包含权限，抛出异常
    if (!permissions.contains(permission)) {
      // 检查接口类型（rest 或者 model）
      final String uriStart = "/api";
      // 获取请求接口
      String uri = HttpContextUtil.getRequestUri();
      if (uri.contains(uriStart)) {
        throw new RestApiException("用户不存在 [ " + permission + " ]权限，无权操作");
      } else {
        throw new ApiException("用户不存在 [ " + permission + " ]权限，无权操作");
      }
    }
    return joinPoint.proceed();
  }
}
