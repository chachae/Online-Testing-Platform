package com.exam.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exam.constant.SysConsts;
import com.exam.entity.Admin;
import com.exam.entity.dto.ChangePassDto;
import com.exam.exception.ServiceException;
import com.exam.mapper.AdminMapper;
import com.exam.service.AdminService;
import com.exam.util.DateUtil;
import com.exam.util.HttpContextUtil;
import com.exam.util.RsaCipherUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 管理员业务实现
 *
 * @author chachae
 * @date 2020/2/10
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

  @Resource private AdminMapper adminMapper;

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
    Date time = DateUtil.getDate();
    this.adminMapper.updateById(Admin.builder().id(admin.getId()).lastLoginTime(time).build());
    return admin;
  }

  @Override
  public Admin selectByNumber(String number) {
    // 通过学号获取学生信息
    QueryWrapper<Admin> qw = new QueryWrapper<>();
    qw.lambda().eq(Admin::getNumber, number);
    return this.adminMapper.selectOne(qw);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void updatePassword(ChangePassDto dto) {
    // 获取该管理员的信息
    Admin admin = this.adminMapper.selectById(dto.getId());
    // 比较旧密码是否输入正确
    if (!RsaCipherUtil.verify(dto.getOldPassword(), admin.getPassword())) {
      throw new ServiceException("旧密码输入错误");
    } else {
      // 旧密码输入正确，进行更新
      Admin build =
          Admin.builder().id(dto.getId()).password(RsaCipherUtil.hash(dto.getPassword())).build();
      this.adminMapper.updateById(build);
    }
  }

  @Override
  public PageInfo<Admin> pageForAdminList(Integer pageNo) {
    // 分页信息设置，默认每页 12 条数据
    PageHelper.startPage(pageNo, 12);
    // 调用管理员查询集合接口
    List<Admin> admins = this.adminMapper.selectList(null);
    return new PageInfo<>(admins);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean removeById(Serializable id) {
    // 获取当前的管理员ID是否和被删除的相同（一样则不能刪除）
    Admin cur = (Admin) HttpContextUtil.getAttribute(SysConsts.SESSION.ADMIN);
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
    entity.setRoleId(SysConsts.ROLE.ADMIN);
    entity.setPassword(RsaCipherUtil.hash(entity.getPassword()));
    entity.setLastLoginTime(DateUtil.getDate());
    return super.save(entity);
  }
}
