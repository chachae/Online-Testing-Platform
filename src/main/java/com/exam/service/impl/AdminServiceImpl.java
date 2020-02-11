package com.exam.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exam.entity.Admin;
import com.exam.entity.Announce;
import com.exam.exception.ServiceException;
import com.exam.mapper.AdminMapper;
import com.exam.mapper.AnnounceMapper;
import com.exam.service.AdminService;
import com.exam.util.DateUtil;
import com.exam.util.RsaCipherUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Stream;

/**
 * 管理员业务实现
 *
 * @author yzn
 * @date 2020/2/10
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

  @Resource private AnnounceMapper announceMapper;
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
    // 使用构造器拼接条件查询 sql
    QueryWrapper<Admin> qw = new QueryWrapper<>();
    qw.lambda().eq(Admin::getNumber, number);
    Admin admin = this.adminMapper.selectOne(qw);
    // 判断管理员对象是否为空对象
    if (ObjectUtil.isEmpty(admin)) {
      throw new ServiceException("管理员帐号不存在");
    }
    // 比较加密后的密码是否一致
    password = RsaCipherUtil.decrypt(password);
    if (!RsaCipherUtil.verify(password, admin.getPassword())) {
      throw new ServiceException("密码错误");
    }
    // 更新登陆时间
    this.adminMapper.updateById(
        Admin.builder().id(admin.getId()).lastLoginTime(DateUtil.getDate()).build());
    return admin;
  }

  /**
   * 查找所有的公告
   *
   * @return 公告列表
   */
  @Override
  public List<Announce> findAllAnnounce() {
    return this.announceMapper.selectList(null);
  }

  /**
   * 根据公告id删除公告
   *
   * @param id 公告ID
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public void delAnnounceById(Integer id) {
    this.announceMapper.deleteById(id);
  }

  /**
   * 批量删除公告
   *
   * @param ids 公告ID集合
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public void delAnnounces(Integer[] ids) {
    Stream.of(ids).forEach(id -> announceMapper.deleteById((id)));
  }

  /**
   * 新增公告
   *
   * @param announce 公告内容
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public void newAnnounce(Announce announce) {
    announce.setCreateTime(DateUtil.getDate());
    announceMapper.insert(announce);
  }
}
