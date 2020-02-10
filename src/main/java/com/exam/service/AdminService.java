package com.exam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.exam.entity.Admin;
import com.exam.entity.Announce;

import java.util.List;

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
   * 查找所有的公告
   *
   * @return 所有公告信息
   */
  List<Announce> findAllAnnounce();

  /**
   * 根据公告id删除公告
   *
   * @param id 公告ID
   */
  void delAnnounceById(Integer id);

  /**
   * 批量删除公告
   *
   * @param ids 公告ID集合
   */
  void delAnnounces(Integer[] ids);

  /**
   * 新增公告
   *
   * @param announce 公告信息
   */
  void newAnnounce(Announce announce);
}
