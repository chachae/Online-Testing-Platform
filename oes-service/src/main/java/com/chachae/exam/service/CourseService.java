package com.chachae.exam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chachae.exam.common.entity.Course;

import java.util.List;

/**
 * 课程服务接口
 *
 * @author yzn
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
}
