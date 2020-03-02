package com.chachae.exam.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chachae.exam.common.constant.SysConsts;
import com.chachae.exam.common.model.Course;
import com.chachae.exam.common.model.Paper;
import com.chachae.exam.common.model.Teacher;
import com.chachae.exam.common.model.dto.ChangePassDto;
import com.chachae.exam.common.exception.ServiceException;
import com.chachae.exam.common.dao.TeacherDAO;
import com.chachae.exam.common.util.RsaCipherUtil;
import com.chachae.exam.service.CourseService;
import com.chachae.exam.service.PaperService;
import com.chachae.exam.service.TeacherService;
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
 * @author chachae
 * @date 2020/2/20
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class TeacherServiceImpl extends ServiceImpl<TeacherDAO, Teacher>
    implements TeacherService {

  @Resource private TeacherDAO teacherDAO;
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
  public void updatePassword(ChangePassDto dto) {
    // 查询该 ID 的教师信息
    Teacher teacher = this.getById(dto.getId());
    if (ObjectUtil.isEmpty(teacher)) {
      throw new ServiceException("用户不存在");
    }

    // 验证密码
    if (!RsaCipherUtil.verify(dto.getOldPassword(), teacher.getPassword())) {
      throw new ServiceException("原密码错误");
    }

    // 封装加密后的密码
    teacher.setPassword(RsaCipherUtil.hash(dto.getPassword()));
    this.updateById(teacher);
  }

  @Override
  public PageInfo<Teacher> pageForTeacherList(Integer pageNo) {
    // 设置分页信息，默认每页显示 12 条数据，此处采用 PageHelper 物理分页插件实现数据分页
    PageHelper.startPage(pageNo, 12);
    List<Teacher> teachers = this.teacherDAO.selectList(null);
    return new PageInfo<>(teachers);
  }

  @Override
  public Teacher selectByWorkNumber(String workNumber) {
    // 查询改用户名的教师信息
    QueryWrapper<Teacher> qw = new QueryWrapper<>();
    qw.lambda().eq(Teacher::getWorkNumber, workNumber);
    return this.teacherDAO.selectOne(qw);
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
      entity.setRoleId(SysConsts.Role.TEACHER);
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
