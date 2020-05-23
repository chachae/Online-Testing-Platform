package com.chachae.exam.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chachae.exam.common.constant.SysConsts;
import com.chachae.exam.common.constant.SysConsts.Role;
import com.chachae.exam.common.dao.MajorDAO;
import com.chachae.exam.common.dao.StudentDAO;
import com.chachae.exam.common.exception.ServiceException;
import com.chachae.exam.common.model.Major;
import com.chachae.exam.common.model.Student;
import com.chachae.exam.common.model.dto.ChangePassDto;
import com.chachae.exam.common.model.dto.QueryStudentDto;
import com.chachae.exam.common.model.dto.StudentExcelDto;
import com.chachae.exam.common.model.vo.StudentVo;
import com.chachae.exam.common.util.FileUtil;
import com.chachae.exam.common.util.PageUtil;
import com.chachae.exam.common.util.RsaCipherUtil;
import com.chachae.exam.service.ScoreService;
import com.chachae.exam.service.StuAnswerRecordService;
import com.chachae.exam.service.StudentService;
import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * 学生业务实现
 *
 * @author chachae
 * @date 2020/1/25
 */
@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class StudentServiceImpl extends ServiceImpl<StudentDAO, Student> implements StudentService {

  private final MajorDAO majorDAO;
  private final StudentDAO studentDAO;
  private final StuAnswerRecordService stuAnswerRecordService;
  private final ScoreService scoreService;

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
    if (student == null) {
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
  @Async
  public synchronized void importStudentsExcel(MultipartFile multipartFile) {
    File file = FileUtil.toFile(multipartFile);
    // 读取 Excel 中的数据
    ExcelReader reader = ExcelUtil.getReader(file);
    // 读取学生的信息
    List<StudentExcelDto> students = reader.read(6, 7, StudentExcelDto.class);
    Student result = new Student();
    for (StudentExcelDto entity : students) {
      // 姓名、学号、专业统一代码不为空才进行导入
      if (StrUtil.isNotBlank(entity.getName()) && StrUtil.isNotBlank(entity.getStuNumber())
          && entity.getMajorId() != null && entity.getLevel() != null) {
        // 同步写入
        // 查询学号是否已存在，不存在则导入
        Student studentResult = this.selectByStuNumber(entity.getStuNumber());
        // 查询专业统一代号是否存在
        Major majorResult = this.majorDAO.selectById(entity.getMajorId());
        if (studentResult == null && majorResult != null) {
          // 性别判断
          String sex;
          if (entity.getSex() == null) {
            sex = null;
          } else {
            sex = entity.getSex() == 1 ? "男" : "女";
          }
          // 参数封装
          result
              .setId(null)
              .setStuNumber(entity.getStuNumber())
              .setMajorId(entity.getMajorId())
              .setSex(sex)
              .setRoleId(Role.STUDENT)
              .setPassword(RsaCipherUtil.hash(SysConsts.DEFAULT_PASSWORD))
              .setName(entity.getName())
              .setLevel(entity.getLevel());
          this.save(result);
        }
      }
    }
    // 手动删除临时文件
    if (!multipartFile.isEmpty()) {
      file.deleteOnExit();
    }
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
      baseMapper.insert(entity);
      return true;
    }
  }

  @Override
  public boolean removeById(Serializable id) {
    // 移除分数和答题记录
    this.scoreService.deleteByStuId((int) id);
    this.stuAnswerRecordService.deleteByStuId((int) id);
    baseMapper.deleteById(id);
    return true;
  }
}
