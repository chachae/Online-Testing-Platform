package com.exam.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exam.entity.Admin;
import com.exam.entity.dto.ChangePassDto;
import com.exam.exception.ServiceException;
import com.exam.mapper.AdminMapper;
import com.exam.service.AdminService;
import com.exam.util.DateUtil;
import com.exam.util.RsaCipherUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 管理员业务实现
 *
 * @author yzn
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
    QueryWrapper<Admin> qw = new QueryWrapper<>();
    qw.lambda().eq(Admin::getNumber, number);
    return this.adminMapper.selectOne(qw);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void updatePassword(Integer id, ChangePassDto dto) {
    // 获取该管理员的信息
    Admin admin = this.adminMapper.selectById(id);
    // 比较旧密码是否输入正确
    if (!RsaCipherUtil.verify(dto.getOldPassword(), admin.getPassword())) {
      throw new ServiceException("旧密码输入错误");
    } else {
      // 旧密码输入正确，进行更新
      Admin build = Admin.builder().id(id).password(RsaCipherUtil.hash(dto.getPassword())).build();
      this.adminMapper.updateById(build);
    }
  }
}
