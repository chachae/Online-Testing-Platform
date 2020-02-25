package com.exam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exam.entity.Course;
import com.exam.entity.Question;
import com.exam.exception.ServiceException;
import com.exam.mapper.CourseMapper;
import com.exam.mapper.QuestionMapper;
import com.exam.mapper.TeacherMapper;
import com.exam.service.CourseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;

/**
 * 课程服务实现类
 *
 * @author yzn
 * @since 2020-02-14 17:48:48
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {

  @Resource private QuestionMapper questionMapper;
  @Resource private CourseMapper courseMapper;
  @Resource private TeacherMapper teacherMapper;

  @Override
  public List<Course> listByTeacherId(Integer teacherId) {
    // 构造查询条件
    QueryWrapper<Course> qw = new QueryWrapper<>();
    qw.lambda().eq(Course::getTeacherId, teacherId);
    List<Course> courses = this.courseMapper.selectList(qw);
    // 为每一门课程设置教师信息
    for (Course course : courses) {
      course.setTeacher(this.teacherMapper.selectById(course.getTeacherId()));
    }
    return courses;
  }

  @Override
  public boolean removeById(Serializable id) {
    // 构造指定课程的试题数数量查询方法
    QueryWrapper<Question> qw = new QueryWrapper<>();
    qw.lambda().eq(Question::getCourseId, id);
    int count = this.questionMapper.selectCount(qw);
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
    course.setTeacher(this.teacherMapper.selectById(course.getTeacherId()));
    return course;
  }
}
