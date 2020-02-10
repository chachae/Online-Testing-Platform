package com.exam.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exam.constant.SysConsts;
import com.exam.entity.Announce;
import com.exam.entity.Course;
import com.exam.entity.Teacher;
import com.exam.entity.dto.ChangePassDto;
import com.exam.exception.ServiceException;
import com.exam.mapper.AnnounceMapper;
import com.exam.mapper.CourseMapper;
import com.exam.mapper.TeacherMapper;
import com.exam.service.TeacherService;
import com.exam.util.RsaCipherUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 教师业务实现
 *
 * @author yzn
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class TeacherServiceImpl extends ServiceImpl<TeacherMapper, Teacher>
    implements TeacherService {

  @Resource private TeacherMapper teacherMapper;
  @Resource private CourseMapper courseMapper;
  @Resource private AnnounceMapper announceMapper;

  /** 管理员角色 ID */
  private static final Integer ROLE_ADMIN_ID = 1;

  @Override
  public Teacher login(String teaNumber, String password) {
    // 查询改用户名的教师信息
    QueryWrapper<Teacher> qw = new QueryWrapper<>();
    qw.lambda().eq(Teacher::getWorkNumber, teaNumber);
    Teacher teacher = this.teacherMapper.selectOne(qw);
    // 判断对象的情况
    if (ObjectUtil.isEmpty(teacher)) {
      throw new ServiceException("该职工号不存在，请重新输入");
    }
    // 比较密码是否相等
    password = RsaCipherUtil.decrypt(password);
    if (!RsaCipherUtil.verify(password, teacher.getPassword())) {
      throw new ServiceException("密码错误");
    }
    return teacher;
  }

  @Override
  public List<Course> findAllCourseByTeacherId(Integer teacherId) {
    // 使用构造器拼接条件查询语句
    QueryWrapper<Course> qw = new QueryWrapper<>();
    qw.lambda().eq(Course::getTeacherId, teacherId);
    return this.courseMapper.selectList(qw);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void delCourseById(Integer id) throws ServiceException {
    // 获取该课程的课程信息
    Course course = courseMapper.selectById(id);
    if (ObjectUtil.isEmpty(course)) {
      throw new ServiceException("该课程不存在！");
    }
    courseMapper.deleteById(id);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void saveCourse(String courseName, Integer teacherId) {
    // 封装课程实体参数
    Teacher teacher = this.getById(teacherId);
    Course course = new Course();
    course.setCourseName(courseName);
    course.setTeacherId(teacherId);
    course.setTeacherName(teacher.getName());
    courseMapper.insert(course);
  }

  @Override
  public List<Announce> findAllSystemAnnounce() {
    // 使用构造器拼接条件查询语句
    QueryWrapper<Announce> qw = new QueryWrapper<>();
    qw.lambda().eq(Announce::getRoleId, ROLE_ADMIN_ID);
    return this.announceMapper.selectList(qw);
  }

  @Override
  public Course findCourseById(Integer courseId) {
    // 调用通过ID查询接口
    return courseMapper.selectById(courseId);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void changePassword(Integer id, ChangePassDto dto) {
    // 查询该 ID 的教师信息
    Teacher teacher = this.getById(id);
    if (ObjectUtil.isEmpty(teacher)) {
      throw new ServiceException("用户不存在");
    }
    if (!RsaCipherUtil.verify(dto.getOldPassword(), teacher.getPassword())) {
      throw new ServiceException("原密码错误");
    }
    teacher.setPassword(RsaCipherUtil.hash(dto.getPassword()));
    this.updateById(teacher);
  }

  @Override
  public PageInfo<Teacher> pageForTeacherList(Integer pageNo) {
    // 设置分页信息，默认每页显示8条数据，此处采用 PageHelper 物理分页插件实现数据分页
    PageHelper.startPage(pageNo, 8);
    List<Teacher> teachers = this.teacherMapper.selectList(null);
    return new PageInfo<>(teachers);
  }

  @Override
  public boolean save(Teacher entity) {
    entity.setPassword(RsaCipherUtil.hash(entity.getPassword()));
    entity.setRoleId(SysConsts.ROLE.TEACHER);
    return super.save(entity);
  }
}
