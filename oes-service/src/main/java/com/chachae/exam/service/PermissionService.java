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

  Set<String> selectExpressionByRoleId(Integer roleId);
}
