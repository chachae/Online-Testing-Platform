package com.chachae.exam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chachae.exam.common.model.Student;
import com.chachae.exam.common.model.dto.ChangePassDto;
import com.chachae.exam.common.model.dto.StudentQueryDto;
import com.chachae.exam.common.model.vo.StudentVo;
import com.github.pagehelper.PageInfo;

/**
 * 学生业务接口
 *
 * @author chachae
 * @date 2020/2/5
 */
public interface StudentService extends IService<Student> {

  /**
   * 学生登录
   *
   * @param stuNumber 学生学号
   * @param stuPassword 学生密码
   * @return 学生信息
   */
  StudentVo login(String stuNumber, String stuPassword);

  /**
   * 学生修改密码
   *
   * @param dto 新的密码
   */
  void updatePassword(ChangePassDto dto);

  /**
   * 通过学号查询学生信息
   *
   * @param stuNumber 学号
   * @return 学生信息
   */
  Student selectByStuNumber(String stuNumber);

  /**
   * 分页查询学生
   *
   * @param pageNo 当前页
   * @param dto 学生查询 bean
   * @return 学生分页结果集
   */
  PageInfo<StudentVo> pageForStudentList(Integer pageNo, StudentQueryDto dto);

  /**
   * 通过 ID 查询学生详细信息
   *
   * @param id 学生 ID
   * @return 学生信息
   */
  StudentVo selectVoById(Integer id);

  /**
   * 通过专业 ID 查询学生数量
   *
   * @param majorId 专业ID
   * @return 学生数量
   */
  Integer selectCountByMajorId(Integer majorId);
}
