package com.exam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.exam.entity.Student;
import com.exam.entity.dto.ChangePassDto;
import com.exam.entity.dto.StudentQueryDto;
import com.exam.entity.vo.StudentVo;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * 学生业务接口
 *
 * @author yzn
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
  Student login(String stuNumber, String stuPassword);

  /**
   * 通过 ID 查询学会说呢过信息
   *
   * @param id 学生ID
   * @return 学生信息
   */
  Student findById(Integer id);

  /**
   * 学生修改密码
   *
   * @param id 学生ID
   * @param dto 新的密码
   */
  void changePassword(Integer id, ChangePassDto dto);

  /**
   * 通过学号查询学生信息
   *
   * @param stuNumber 学号
   * @return 学生信息
   */
  Student findByStuNumber(String stuNumber);

  /**
   * 分页查询学生
   *
   * @param pageNo 当前页
   * @param dto 学生查询 bean
   * @return 学生分页结果集
   */
  PageInfo<StudentVo> pageForStudentList(Integer pageNo, StudentQueryDto dto);

  /**
   * 获取 StudentVo 对象
   *
   * @param id 查询条件
   * @return 学生信息
   */
  StudentVo findVoById(Integer id);

  /**
   * 获取 StudentVo 对象
   *
   * @param number 学号
   * @return 学生信息
   */
  StudentVo findVoByStuNumber(String number);

  /**
   * 获取 StudentVo List 集合对象
   *
   * @param dto 查询条件
   * @return 学生 List 集合信息
   */
  List<StudentVo> listVo(StudentQueryDto dto);
}
