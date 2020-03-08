package com.chachae.exam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chachae.exam.common.model.Course;

import java.util.List;

/**
 * 课程服务接口
 *
 * @author chachae
 * @since 2020-02-14 17:48:48
 */
public interface CourseService extends IService<Course> {

  /**
   * 查询一个老师所教的所有课程
   *
   * @param teacherId 教师ID
   * @return 课程集合
   */
  List<Course> listByTeacherId(Integer teacherId);

  /**
   * 通过课程名称查询课程集合
   *
   * @param courseName 课程名称
   * @return 课程集合信息
   */
  List<Course> selectByCourseName(String courseName);
}
