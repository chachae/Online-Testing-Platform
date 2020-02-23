package com.exam.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exam.constant.SysConsts;
import com.exam.entity.Course;
import com.exam.entity.Paper;
import com.exam.entity.Teacher;
import com.exam.entity.dto.ChangePassDto;
import com.exam.exception.ServiceException;
import com.exam.mapper.TeacherMapper;
import com.exam.service.CourseService;
import com.exam.service.PaperService;
import com.exam.service.TeacherService;
import com.exam.util.RsaCipherUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.Serializable;
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
  @Resource private CourseService courseService;
  @Resource private PaperService paperService;

  @Override
  public Teacher login(String teaNumber, String password) {
    // 查询改用户名的教师信息
    Teacher teacher = this.selectByWorkNumber(teaNumber);
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
  @Transactional(rollbackFor = Exception.class)
  public void updatePassword(Integer id, ChangePassDto dto) {
    // 查询该 ID 的教师信息
    Teacher teacher = this.getById(id);
    if (ObjectUtil.isEmpty(teacher)) {
      throw new ServiceException("用户不存在");
    }
    // 验证密码
    if (!RsaCipherUtil.verify(dto.getOldPassword(), teacher.getPassword())) {
      throw new ServiceException("原密码错误");
    }
    teacher.setPassword(RsaCipherUtil.hash(dto.getPassword()));
    this.updateById(teacher);
  }

  @Override
  public PageInfo<Teacher> pageForTeacherList(Integer pageNo) {
    // 设置分页信息，默认每页显示 12 条数据，此处采用 PageHelper 物理分页插件实现数据分页
    PageHelper.startPage(pageNo, 12);
    List<Teacher> teachers = this.teacherMapper.selectList(null);
    return new PageInfo<>(teachers);
  }

  @Override
  public Teacher selectByWorkNumber(String workNumber) {
    // 查询改用户名的教师信息
    QueryWrapper<Teacher> qw = new QueryWrapper<>();
    qw.lambda().eq(Teacher::getWorkNumber, workNumber);
    return this.teacherMapper.selectOne(qw);
  }

  @Override
  public boolean save(Teacher entity) {
    // 查询是否已经存在该工号
    Teacher teacher = this.selectByWorkNumber(entity.getWorkNumber());
    if (ObjectUtil.isNotEmpty(teacher)) {
      throw new ServiceException("工号已存在");
    } else {
      // 不存在则设置默认的角色ID和对密码进行加密存储
      entity.setPassword(RsaCipherUtil.hash(entity.getPassword()));
      entity.setRoleId(SysConsts.ROLE.TEACHER);
      return super.save(entity);
    }
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean removeById(Serializable id) {
    // 判断关联关系，（试卷和课程，不存在关联再进行删除）
    List<Course> courses = this.courseService.listByTeacherId((int) id);
    if (CollUtil.isNotEmpty(courses)) {
      throw new ServiceException("该教师存在课程关联，不允许删除");
    }
    List<Paper> papers = this.paperService.listByTeacherId((int) id);
    if (CollUtil.isNotEmpty(papers)) {
      throw new ServiceException("该教师存在试卷关联，不允许删除");
    }
    // 支持，允许被执行删除
    return super.removeById(id);
  }
}
