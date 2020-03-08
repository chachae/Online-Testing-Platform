package com.chachae.exam.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chachae.exam.common.constant.SysConsts;
import com.chachae.exam.common.dao.AdminDAO;
import com.chachae.exam.common.exception.ServiceException;
import com.chachae.exam.common.model.Admin;
import com.chachae.exam.common.model.dto.ChangePassDto;
import com.chachae.exam.common.util.DateUtil;
import com.chachae.exam.common.util.HttpContextUtil;
import com.chachae.exam.common.util.PageUtil;
import com.chachae.exam.common.util.RsaCipherUtil;
import com.chachae.exam.service.AdminService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * 管理员业务实现
 *
 * @author chachae
 * @date 2020/2/10
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class AdminServiceImpl extends ServiceImpl<AdminDAO, Admin> implements AdminService {

  @Resource private AdminDAO adminDAO;

  /**
   * 管理员登录
   *
   * @param number 管理员账号
   * @param password 管理员密码
   * @return 管理员信息
   */
  @Override
  public Admin login(String number, String password) {
    // 调用通过账号查询信息的方法
    Admin admin = this.selectByNumber(number);
    // 判断管理员对象是否为空对象
    if (ObjectUtil.isEmpty(admin)) {
      throw new ServiceException("管理员帐号不存在");
    }
    // 私钥解密，并比较加密后的密码是否一致
    password = RsaCipherUtil.decrypt(password);
    if (!RsaCipherUtil.verify(password, admin.getPassword())) {
      throw new ServiceException("密码错误");
    }
    // 更新登陆时间
    Date time = cn.hutool.core.date.DateUtil.date();
    this.adminDAO.updateById(Admin.builder().id(admin.getId()).lastLoginTime(time).build());
    return admin;
  }

  @Override
  public Admin selectByNumber(String number) {
    // 通过学号获取学生信息
    QueryWrapper<Admin> qw = new QueryWrapper<>();
    qw.lambda().eq(Admin::getNumber, number);
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
  public Map<String, Object> listPage(Page<Admin> page) {
    Page<Admin> pageInfo = this.adminDAO.selectPage(page, null);
    return PageUtil.toPage(pageInfo);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean removeById(Serializable id) {
    // 获取当前的管理员ID是否和被删除的相同（一样则不能刪除）
    Admin cur = (Admin) HttpContextUtil.getAttribute(SysConsts.Session.ADMIN);
    // 当前 session 的 管理员 ID 和被删除的管理员ID一直，不能被删除
    if (cur.getId().equals(id)) {
      throw new ServiceException("不可以删除自己");
    }
    // ID 不相同，允许删除
    return super.removeById(id);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean save(Admin entity) {
    // 检测是否存在相同的用户名，存在在不允许增加，抛出异常，给控制层捕捉
    Admin admin = this.selectByNumber(entity.getNumber());
    if (ObjectUtil.isNotEmpty(admin)) {
      throw new ServiceException("用户名已存在");
    }
    // 封装管理员默认角色ID，同时加密密码
    entity.setRoleId(SysConsts.Role.ADMIN);
    entity.setPassword(RsaCipherUtil.hash(entity.getPassword()));
    entity.setLastLoginTime(DateUtil.date());
    return super.save(entity);
  }
}
