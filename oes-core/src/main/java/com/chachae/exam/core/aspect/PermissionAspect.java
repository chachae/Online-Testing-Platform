package com.chachae.exam.core.aspect;

import com.chachae.exam.common.constant.SysConsts;
import com.chachae.exam.common.exception.NoPermissionException;
import com.chachae.exam.common.util.HttpUtil;
import com.chachae.exam.core.annotation.Permissions;
import com.chachae.exam.service.PermissionService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * 权限校验切面
 *
 * @author chachae
 * @since 2020/3/1 17:36
 */
@Slf4j
@Aspect
@Order(0)
@Component
public class PermissionAspect extends BaseAspectSupport {

  @Resource private PermissionService permissionService;

  /** 切点：带@Permission 注解的方法/接口 */
  @Pointcut("@annotation(com.chachae.exam.core.annotation.Permissions)")
  public void permissionPointCut() {}

  /**
   * 配置环绕通知
   *
   * @param joinPoint 切点
   */
  @Around("permissionPointCut()")
  public Object permissionCheckAround(ProceedingJoinPoint joinPoint) throws Throwable {

    // 获取用户角色信息（session 情况交给拦截器处理，此处不用管）
    int roleId = (int) HttpUtil.getAttribute(SysConsts.Session.ROLE_ID);
    Set<String> permissions = this.permissionService.selectExpressionByRoleId(roleId);

    // 获取注解信息
    Method method = resolveMethod(joinPoint);
    Permissions annotation = method.getAnnotation(Permissions.class);
    String expression = annotation.value();
    // 不包含权限，抛出异常
    if (!permissions.contains(expression)) {
      throw new NoPermissionException("用户不存在 [ " + expression + " ] 权限，无权操作");
    }
    return joinPoint.proceed();
  }
}
