package com.chachae.exam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chachae.exam.common.entity.Admin;
import com.chachae.exam.common.entity.dto.ChangePassDto;
import com.github.pagehelper.PageInfo;

/**
 * 管理员业务接口
 *
 * @author yzn
 */
public interface AdminService extends IService<Admin> {

  /**
   * 管理员登录
   *
   * @param number 管理员账号
   * @param password 管理员密码
   * @return 管理员信息
   */
  Admin login(String number, String password);

  /**
   * 通过管理员账号查询管理员信息
   *
   * @param number 管理员账号
   * @return 管理员信息
   */
  Admin selectByNumber(String number);

  /**
   * 修改股管理员密码
   *
   * @param dto 密码信息
   */
  void updatePassword(ChangePassDto dto);

  /**
   * 获取管理员分页信息
   *
   * @param pageNo 当前页
   * @return 管理员分页结果集
   */
  PageInfo<Admin> pageForAdminList(Integer pageNo);
}
