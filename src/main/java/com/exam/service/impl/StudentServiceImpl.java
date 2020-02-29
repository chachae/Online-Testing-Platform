package com.exam.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exam.constant.SysConsts;
import com.exam.entity.Student;
import com.exam.entity.dto.ChangePassDto;
import com.exam.entity.dto.StudentQueryDto;
import com.exam.entity.vo.StudentVo;
import com.exam.exception.ServiceException;
import com.exam.mapper.StudentMapper;
import com.exam.service.ScoreService;
import com.exam.service.StuAnswerRecordService;
import com.exam.service.StudentService;
import com.exam.util.RsaCipherUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

/**
 * 学生业务实现
 *
 * @author yzn
 * @date 2020/1/25
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student>
    implements StudentService {

  @Resource private StudentMapper studentMapper;
  @Resource private StuAnswerRecordService stuAnswerRecordService;
  @Resource private ScoreService scoreService;

  @Override
  public Student login(String stuNumber, String password) throws ServiceException {
    // 调用通过学号查询学生查询接口
    Student student = this.selectByStuNumber(stuNumber);

    // 如果学生对象为空说明不存在该用户，抛出异常信息
    if (ObjectUtil.isEmpty(student)) {
      throw new ServiceException("该学号不存在，请重新输入");
    }

    // 加密密码进行比较，比较通过登陆通过，反之说明密码错误
    password = RsaCipherUtil.decrypt(password);
    if (!RsaCipherUtil.verify(password, student.getPassword())) {
      throw new ServiceException("密码错误");
    }
    return student;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void updatePassword(ChangePassDto dto) {
    // 通过 ID 查询学生信息
    Student student = studentMapper.selectById(dto.getId());
    if (ObjectUtil.isEmpty(student)) {
      throw new ServiceException("用户不存在");
    }

    if (!RsaCipherUtil.verify(dto.getOldPassword(), student.getPassword())) {
      throw new ServiceException("原密码错误");
    }

    // 加密密码之后再存入数据库
    student.setPassword(RsaCipherUtil.hash(dto.getPassword()));
    // 执行密码修改
    studentMapper.updateById(student);
  }

  @Override
  public Student selectByStuNumber(String stuNumber) {
    // 使用 QueryWrapper 构造通过学号查询条件
    QueryWrapper<Student> qw = new QueryWrapper<>();
    qw.lambda().eq(Student::getStuNumber, stuNumber);
    // 通过学号查询学生信息
    return studentMapper.selectOne(qw);
  }

  @Override
  public PageInfo<StudentVo> pageForStudentList(Integer pageNo, StudentQueryDto dto) {
    // 设置分页信息，默认每页显示8条数据，此处采用 PageHelper 物理分页插件实现数据分页
    PageHelper.startPage(pageNo, 12);
    List<StudentVo> students = this.studentMapper.listVo(dto);
    // 按照学院 id 从小到大排序
    students.sort(Comparator.comparingInt(e -> e.getAcademy().getId()));
    return new PageInfo<>(students);
  }

  @Override
  public StudentVo selectVoById(Integer id) {
    return this.studentMapper.selectVoById(id);
  }

  @Override
  public Integer selectCountByMajorId(Integer majorId) {
    QueryWrapper<Student> qw = new QueryWrapper<>();
    qw.lambda().eq(Student::getMajorId, majorId);
    return this.studentMapper.selectCount(qw);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean save(Student entity) {
    // 检测学号是否存在
    Student student = this.selectByStuNumber(entity.getStuNumber());
    if (ObjectUtil.isNotEmpty(student)) {
      throw new ServiceException("学号已存在");
    } else {
      // 默认角色值2
      entity.setRoleId(SysConsts.ROLE.STUDENT);
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
