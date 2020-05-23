package com.chachae.exam.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chachae.exam.common.model.Teacher;
import com.chachae.exam.common.model.dto.ChangePassDto;
import com.chachae.exam.common.model.dto.QueryTeacherDto;
import java.util.Map;

/**
 * 教师业务接口
 *
 * @author chachae
 * @date 2020/2/5
 */
public interface TeacherService extends IService<Teacher> {

  /**
   * 教师登录
   *
   * @param teacherId       教师ID
   * @param teacherPassword 教师密码
   * @return 教师信息
   */
  Teacher login(String teacherId, String teacherPassword);

  /**
   * 修改密码
   *
   * @param dto 密码信息
   */
  void updatePassword(ChangePassDto dto);

  /**
   * 通过工号查询教师信息
   *
   * @param workNumber 工号
   * @return 教师信息
   */
  Teacher selectByWorkNumber(String workNumber);

  /**
   * 分页查询教师信息
   *
   * @param page   分页信息
   * @param entity 查询实体
   * @return 分页结果集
   */
  Map<String, Object> listPage(Page<Teacher> page, QueryTeacherDto entity);
}
