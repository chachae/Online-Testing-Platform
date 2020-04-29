package com.chachae.exam.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chachae.exam.common.constant.SysConsts;
import com.chachae.exam.common.dao.StudentDAO;
import com.chachae.exam.common.exception.ServiceException;
import com.chachae.exam.common.model.Student;
import com.chachae.exam.common.model.dto.ChangePassDto;
import com.chachae.exam.common.model.dto.QueryStudentDto;
import com.chachae.exam.common.model.vo.StudentVo;
import com.chachae.exam.common.util.PageUtil;
import com.chachae.exam.common.util.RsaCipherUtil;
import com.chachae.exam.service.ScoreService;
import com.chachae.exam.service.StuAnswerRecordService;
import com.chachae.exam.service.StudentService;
import java.io.Serializable;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 学生业务实现
 *
 * @author chachae
 * @date 2020/1/25
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class StudentServiceImpl extends ServiceImpl<StudentDAO, Student> implements StudentService {

  @Resource
  private StudentDAO studentDAO;
  @Resource
  private StuAnswerRecordService stuAnswerRecordService;
  @Resource
  private ScoreService scoreService;

  @Override
  public StudentVo login(String stuNumber, String password) {
    // 调用通过学号查询学生查询接口
    Student student = this.selectByStuNumber(stuNumber);

    // 如果学生对象为空说明不存在该用户，抛出异常信息
    if (student == null) {
      throw new ServiceException("该学号不存在");
    }

    // 加密密码进行比较，比较通过登陆通过，反之说明密码错误
    password = RsaCipherUtil.decrypt(password);
    if (!RsaCipherUtil.verify(password, student.getPassword())) {
      throw new ServiceException("密码错误");
    }

    return this.selectVoById(student.getId());
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void updatePassword(ChangePassDto dto) {
    // 通过 ID 查询学生信息
    Student student = studentDAO.selectById(dto.getId());
    if (ObjectUtil.isEmpty(student)) {
      throw new ServiceException("用户身份异常");
    }

    if (!RsaCipherUtil.verify(dto.getOldPassword(), student.getPassword())) {
      throw new ServiceException("原密码错误");
    }

    // 加密密码之后再存入数据库
    student.setPassword(RsaCipherUtil.hash(dto.getPassword()));
    // 执行密码修改
    studentDAO.updateById(student);
  }

  @Override
  public Student selectByStuNumber(String stuNumber) {
    // 使用 QueryWrapper 构造通过学号查询条件
    LambdaQueryWrapper<Student> qw = new LambdaQueryWrapper<>();
    qw.eq(Student::getStuNumber, stuNumber);
    // 通过学号查询学生信息
    return studentDAO.selectOne(qw);
  }

  @Override
  public Map<String, Object> listPage(Page<Student> page, QueryStudentDto entity) {
    IPage<StudentVo> pageInfo = this.studentDAO.pageVo(page, entity);
    return PageUtil.toPage(pageInfo);
  }

  @Override
  public StudentVo selectVoById(Integer id) {
    return this.studentDAO.selectVoById(id);
  }

  @Override
  public Integer selectCountByMajorId(Integer majorId) {
    LambdaQueryWrapper<Student> qw = new LambdaQueryWrapper<>();
    qw.eq(Student::getMajorId, majorId);
    return this.studentDAO.selectCount(qw);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean save(Student entity) {
    // 检测学号是否存在
    Student student = this.selectByStuNumber(entity.getStuNumber());
    if (student != null) {
      throw new ServiceException("学号已存在");
    } else {
      // 默认角色值2
      entity.setRoleId(SysConsts.Role.STUDENT);
      // 密码加密之后再存入数据库
      entity.setPassword(RsaCipherUtil.hash(entity.getPassword()));
      // 调用父级 save 接口
      return super.save(entity);
    }
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean removeById(Serializable id) {
    // 移除分数和答题记录
    this.scoreService.deleteByStuId((int) id);
    this.stuAnswerRecordService.deleteByStuId((int) id);
    // 调用父级 remove 接口
    return super.removeById(id);
  }
}
