package com.chachae.exam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chachae.exam.common.dao.CourseDAO;
import com.chachae.exam.common.dao.QuestionDAO;
import com.chachae.exam.common.dao.TeacherDAO;
import com.chachae.exam.common.exception.ServiceException;
import com.chachae.exam.common.model.Course;
import com.chachae.exam.common.model.Question;
import com.chachae.exam.service.CourseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;

/**
 * 课程服务实现类
 *
 * @author chachae
 * @since 2020-02-14 17:48:48
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class CourseServiceImpl extends ServiceImpl<CourseDAO, Course> implements CourseService {

  @Resource private QuestionDAO questionDAO;
  @Resource private CourseDAO courseDAO;
  @Resource private TeacherDAO teacherDAO;

  @Override
  public List<Course> listByTeacherId(Integer teacherId) {
    // 构造查询条件
    QueryWrapper<Course> qw = new QueryWrapper<>();
    qw.lambda().eq(Course::getTeacherId, teacherId);
    List<Course> courses = this.courseDAO.selectList(qw);
    // 为每一门课程设置教师信息
    for (Course course : courses) {
      course.setTeacher(this.teacherDAO.selectById(course.getTeacherId()));
    }
    return courses;
  }

  @Override
  public boolean removeById(Serializable id) {
    // 构造指定课程的试题数数量查询方法
    QueryWrapper<Question> qw = new QueryWrapper<>();
    qw.lambda().eq(Question::getCourseId, id);
    int count = this.questionDAO.selectCount(qw);
    // 如果数量大于0，不能删除该课程，直接抛出异常
    if (count > 0) {
      throw new ServiceException("课程存在试题关联，请删除后再尝试。");
    } else {
      return super.removeById(id);
    }
  }

  @Override
  public Course getById(Serializable id) {
    Course course = super.getById(id);
    // 为课程设置教师信息
    course.setTeacher(this.teacherDAO.selectById(course.getTeacherId()));
    return course;
  }
}
