package com.chachae.exam.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chachae.exam.common.constant.SysConsts;
import com.chachae.exam.common.dao.PermissionDAO;
import com.chachae.exam.common.model.Permission;
import com.chachae.exam.common.model.RolePermission;
import com.chachae.exam.service.PermissionService;
import com.chachae.exam.service.RolePermissionService;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * (Permission)表服务实现类
 *
 * @author chachae
 * @since 2020-03-01 16:01:52
 */
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionDAO, Permission>
    implements PermissionService {

  @Value("${oes.cache.permission_prefix}")
  private String prefix;

  @Resource private PermissionDAO permissionDAO;
  @Resource private RedisTemplate<String, Object> redisService;
  @Resource private RolePermissionService rolePermissionService;

  @Override
  public Set<String> selectExpressionByRoleId(Integer roleId) {
    return permissionManager(roleId);
  }

  /**
   * 权限缓存管理器
   *
   * @param roleId 角色 ID
   */
  @SuppressWarnings("unchecked")
  private Set<String> permissionManager(Integer roleId) {
    // 缓存键
    final String key = prefix + roleId;
    // 从缓存中获取权限信息
    Object obj = redisService.opsForValue().get(key);
    if (ObjectUtil.isNotEmpty(obj)) {
      return (Set<String>) obj;
    }

    // 组装权限信息
    Set<String> result = Sets.newHashSet();
    // 判断角色等级
    if (roleId.equals(SysConsts.Role.ADMIN)) {
      // 管理员直接拥有所有权限表达式
      List<Permission> permissions = this.permissionDAO.selectList(null);
      permissions.forEach(pm -> result.add(pm.getExpression()));
    } else {
      List<RolePermission> rolePermissions = this.rolePermissionService.selectByRoleId(roleId);
      // 取出权限信息
      List<Integer> permissionIds = Lists.newArrayList();
      rolePermissions.forEach(rp -> permissionIds.add(rp.getPermissionId()));
      // 查询权限表达式
      if (CollUtil.isNotEmpty(permissionIds)) {
        List<Permission> permissions = this.permissionDAO.selectBatchIds(permissionIds);
        permissions.forEach(pm -> result.add(pm.getExpression()));
      }
    }

    // 缓存到 redis 中
    this.redisService.opsForValue().set(key, result, 3 * 3600L, TimeUnit.SECONDS);
    return result;
  }
}
