package com.chachae.exam.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chachae.exam.common.constant.SysConsts.Role;
import com.chachae.exam.common.constant.SysConsts.Session;
import com.chachae.exam.common.dao.AdminDAO;
import com.chachae.exam.common.exception.ServiceException;
import com.chachae.exam.common.model.Admin;
import com.chachae.exam.common.model.dto.ChangePassDto;
import com.chachae.exam.common.model.dto.QueryAdminDto;
import com.chachae.exam.common.util.HttpUtil;
import com.chachae.exam.common.util.PageUtil;
import com.chachae.exam.common.util.RsaCipherUtil;
import com.chachae.exam.service.AdminService;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 管理员业务实现
 *
 * @author chachae
 * @date 2020/2/10
 */
@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class AdminServiceImpl extends ServiceImpl<AdminDAO, Admin> implements AdminService {

  private final AdminDAO adminDAO;

  /**
   * 管理员登录
   *
   * @param number   管理员账号
   * @param password 管理员密码
   * @return 管理员信息
   */
  @Override
  public Admin login(String number, String password) {
    // 调用通过账号查询信息的方法
    Admin admin = this.selectByNumber(number);
    // 判断管理员对象是否为空对象
    if (admin == null) {
      throw new ServiceException("管理员帐号不存在");
    }
    // 私钥解密，并比较加密后的密码是否一致
    password = RsaCipherUtil.decrypt(password);
    if (!RsaCipherUtil.verify(password, admin.getPassword())) {
      throw new ServiceException("密码错误");
    }
    // 更新登陆时间
    this.adminDAO.updateById(Admin.builder().id(admin.getId()).lastLoginTime(new Date()).build());
    return admin;
  }

  @Override
  public Admin selectByNumber(String number) {
    // 通过学号获取学生信息
    LambdaQueryWrapper<Admin> qw = new LambdaQueryWrapper<>();
    qw.eq(Admin::getNumber, number);
    return this.adminDAO.selectOne(qw);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void updatePassword(ChangePassDto dto) {
    // 获取该管理员的信息
    Admin admin = this.adminDAO.selectById(dto.getId());
    // 比较旧密码是否输入正确
    if (!RsaCipherUtil.verify(dto.getOldPassword(), admin.getPassword())) {
      throw new ServiceException("旧密码输入错误");
    } else {
      // 旧密码输入正确，进行更新
      Admin build =
          Admin.builder().id(dto.getId()).password(RsaCipherUtil.hash(dto.getPassword())).build();
      this.adminDAO.updateById(build);
    }
  }

  @Override
  public Map<String, Object> listPage(Page<Admin> page, QueryAdminDto entity) {
    LambdaQueryWrapper<Admin> qw = new LambdaQueryWrapper<>();
    // academyId = 0 说明是系统管理员
    if (entity.getAcademyId() != null) {
      qw.eq(Admin::getAcademyId, entity.getAcademyId());
    }
    if (StrUtil.isNotBlank(entity.getKey())) {
      qw.like(Admin::getNumber, entity.getKey()).or().like(Admin::getName, entity.getKey());
    }
    Page<Admin> pageInfo = this.adminDAO.selectPage(page, qw);
    return PageUtil.toPage(pageInfo);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean removeById(Serializable id) {
    // 获取当前的管理员ID是否和被删除的相同（一样则不能刪除）
    Admin cur = (Admin) HttpUtil.getAttribute(Session.ADMIN);
    // 当前 session 的 管理员 ID 和被删除的管理员ID一直，不能被删除
    if (cur.getId().equals(id)) {
      throw new ServiceException("不可以删除自己");
    }
    // ID 不相同，允许删除
    baseMapper.deleteById(id);
    return true;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean save(Admin entity) {
    // 检测是否存在相同的用户名，存在在不允许增加，抛出异常，给控制层捕捉
    Admin admin = this.selectByNumber(entity.getNumber());
    if (admin != null) {
      throw new ServiceException("用户名已存在");
    }
    // 系统管理员不需要设置academyId
    if (entity.getAcademyId() == 0) {
      entity.setAcademyId(null);
    }
    // 封装管理员默认角色ID，同时加密密码
    entity.setRoleId(Role.ADMIN);
    entity.setPassword(RsaCipherUtil.hash(entity.getPassword()));
    entity.setLastLoginTime(new Date());
    baseMapper.insert(entity);
    return true;
  }
}
