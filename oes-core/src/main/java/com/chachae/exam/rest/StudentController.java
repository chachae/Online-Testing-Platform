package com.chachae.exam.rest;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chachae.exam.common.base.R;
import com.chachae.exam.common.constant.SysConsts;
import com.chachae.exam.common.model.Student;
import com.chachae.exam.common.model.dto.ChangePassDto;
import com.chachae.exam.common.model.dto.LoginDto;
import com.chachae.exam.common.model.dto.QueryStudentDto;
import com.chachae.exam.common.model.vo.StudentVo;
import com.chachae.exam.common.util.HttpContextUtil;
import com.chachae.exam.core.annotation.Limit;
import com.chachae.exam.core.annotation.Permissions;
import com.chachae.exam.service.StudentService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Map;

/**
 * @author chachae
 * @since 2020/2/28 22:31
 */
@RestController
@RequestMapping("/api/student")
public class StudentController {

  @Resource private StudentService studentService;

  /**
   * 学生登录 验证学号和密码
   *
   * @param entity 账号密码
   * @return 主界面
   */
  @PostMapping("/login")
  public R login(@Valid LoginDto entity) {
    // 获取 session 对象
    HttpSession session = HttpContextUtil.getSession();
    // 执行登录接口
    StudentVo student = studentService.login(entity.getUsername(), entity.getPassword());
    // 设置 Session 信息
    session.setAttribute(SysConsts.Session.ROLE_ID, student.getRoleId());
    session.setAttribute(SysConsts.Session.STUDENT_ID, student.getId());
    session.setAttribute(SysConsts.Session.STUDENT, student);
    // 重定向到学生主界面
    return R.successWithData(student.getId());
  }

  @GetMapping("/list")
  @Permissions("student:list")
  @Limit(key = "majorList", period = 5, count = 15, name = "学生查询接口", prefix = "limit")
  public Map<String,Object> listPage(Page<Student> page, QueryStudentDto entity){
    return this.studentService.listPage(page,entity);
  }

  /**
   * 密码修改
   *
   * @param dto 密码信息
   * @return 重定向到登录界面
   */
  @PostMapping("/update/password")
  @Permissions("student:update:password")
  public R updatePassword(@Valid ChangePassDto dto) {
    // 通过获取 Session 对象
    HttpSession session = HttpContextUtil.getSession();
    // 调用密码修改接口
    this.studentService.updatePassword(dto);
    // 移除学生 session 信息
    session.removeAttribute(SysConsts.Session.ROLE_ID);
    session.removeAttribute(SysConsts.Session.STUDENT_ID);
    session.removeAttribute(SysConsts.Session.STUDENT);
    return R.success();
  }

  /**
   * 更新学生
   *
   * @param student 学生信息
   * @return 成功信息
   */
  @PostMapping("/update")
  @Permissions("student:update")
  public R updateStudent(Student student) {
    this.studentService.updateById(student);
    return R.success();
  }

  /**
   * 删除学生
   *
   * @param id 学生ID
   * @return 成功信息
   */
  @PostMapping("/delete/{id}")
  @Permissions("student:delete")
  public R deleteStudent(@PathVariable Integer id) {
    this.studentService.removeById(id);
    return R.success();
  }

  /**
   * 增加学生
   *
   * @param student 学生信息
   * @return 成功信息
   */
  @PostMapping("/save")
  @Permissions("student:save")
  public R saveStudent(Student student) {
    // 调用增加接口，并捕捉学号存在的异常
    this.studentService.save(student);
    return R.success();
  }

  /**
   * 获取一个学生
   *
   * @param id 学生 ID
   * @return 学生信息
   */
  @GetMapping("/{id}")
  @Permissions("student:list")
  public StudentVo getOne(@PathVariable Integer id) {
    return this.studentService.selectVoById(id);
  }
}
