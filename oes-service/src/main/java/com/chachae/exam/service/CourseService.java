package com.chachae.exam.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chachae.exam.common.model.Course;

import java.util.List;
import java.util.Map;

/**
 * 课程服务接口
 *
 * @author chachae
 * @since 2020-02-14 17:48:48
 */
public interface CourseService extends IService<Course> {

  /**
   * 查询课程 ID 集合
   *
   * @param teacherId 教师 ID
   * @return 教师所有课程的 ID
   */
  List<Integer> listIdByTeacherId(Integer teacherId);

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

  /**
   * 分页查询课程信息
   *
   * @param page 分页数据
   * @param teacherId 教师ID
   * @return 分页结果集
   */
  Map<String, Object> listPage(Page<Course> page, Integer teacherId);
}
