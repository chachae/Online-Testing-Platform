package com.chachae.exam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chachae.exam.common.dao.RolePermissionDAO;
import com.chachae.exam.common.model.RolePermission;
import com.chachae.exam.service.RolePermissionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * (RolePermission)表服务实现类
 *
 * @author chachae
 * @since 2020-03-01 16:02:05
 */
@Service
public class RolePermissionServiceImpl extends ServiceImpl<RolePermissionDAO, RolePermission>
    implements RolePermissionService {

  @Resource private RolePermissionDAO rolePermissionDAO;

  @Override
  public List<RolePermission> selectByRoleId(Integer roleId) {
    QueryWrapper<RolePermission> rpQw = new QueryWrapper<>();
    rpQw.lambda().eq(RolePermission::getRoleId, roleId);
    return this.rolePermissionDAO.selectList(rpQw);
  }
}
