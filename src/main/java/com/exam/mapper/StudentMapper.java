package com.exam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.exam.entity.Student;
import com.exam.entity.dto.StudentQueryDto;
import com.exam.entity.vo.StudentVo;

import java.util.List;

/**
 * 学生 Mapper 接口
 *
 * @author yzn
 * @date 2020/1/6
 */
public interface StudentMapper extends BaseMapper<Student> {

  /**
   * 通过学号查询学生信息
   *
   * @param stuNumber 学号
   * @return 学生信息
   */
  StudentVo selectVoByStuNumber(String stuNumber);

  /**
   * 通过条件查询学生 List 集合
   *
   * @param dto 学生查询条件
   * @return 学生 List 集合
   */
  List<StudentVo> listVo(StudentQueryDto dto);
}
