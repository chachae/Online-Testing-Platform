package com.chachae.exam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chachae.exam.common.model.Permission;
import java.util.Set;

/**
 * (Permission)表服务接口
 *
 * @author chachae
 * @since 2020-03-01 16:01:52
 */
public interface PermissionService extends IService<Permission> {

  /**
   * 获取用户权限表达式
   *
   * @param roleId 角色id
   * @return 权限表达式集合：Set
   */
  Set<String> selectExpressionByRoleId(Integer roleId);
}
