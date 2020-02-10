package com.exam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.exam.entity.Announce;
import com.exam.entity.Course;
import com.exam.entity.Teacher;
import com.exam.entity.dto.ChangePassDto;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * 教师业务接口
 *
 * @author yzn
 * @date 2020/2/5
 */
public interface TeacherService extends IService<Teacher> {

  /**
   * 教师登录
   *
   * @param teacherId 教师ID
   * @param teacherPassword 教师密码
   * @return 教师信息
   */
  Teacher login(String teacherId, String teacherPassword);

  /**
   * 根据教师id查找所教课程
   *
   * @param teacherId 教师ID
   * @return 课程集合
   */
  List<Course> findAllCourseByTeacherId(Integer teacherId);

  /**
   * 根据课程id删除该课程
   *
   * @param id 课程ID
   */
  void delCourseById(Integer id);

  /**
   * 添加课程
   *
   * @param courseName 课程名称
   * @param teacherId 教师ID
   */
  void saveCourse(String courseName, Integer teacherId);

  /**
   * 查找所有的系统公告
   *
   * @return 公告集合
   */
  List<Announce> findAllSystemAnnounce();

  /**
   * 通过课程 ID 查询课程信息
   *
   * @param courseId 课程ID
   * @return 课程信息
   */
  Course findCourseById(Integer courseId);

  /**
   * 修改密码
   *
   * @param id 教师ID
   * @param dto 密码信息
   */
  void changePassword(Integer id, ChangePassDto dto);

  /**
   * 分页查询教师列表
   *
   * @param pageNo 当前页
   * @return 教师数据分页结果集
   */
  PageInfo<Teacher> pageForTeacherList(Integer pageNo);
}
