package com.exam.rest;

import com.exam.common.R;
import com.exam.constant.SysConsts;
import com.exam.entity.Teacher;
import com.exam.entity.dto.ChangePassDto;
import com.exam.service.TeacherService;
import com.exam.util.HttpContextUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 * @author chachae
 * @since 2020/2/28 12:00
 */
@RestController
@RequestMapping("/api/teacher")
public class TeacherRest {

  @Resource private TeacherService teacherService;

  /**
   * 教师登录
   *
   * @param teacherId 教师ID
   * @param password 教师密码
   * @return 教师主页
   */
  @PostMapping("/login")
  public R login(String teacherId, String password) {
    // 获取 session 对象
    HttpSession session = HttpContextUtil.getSession();
    // 执行登录
    Teacher teacher = teacherService.login(teacherId, password);
    // 设置 session 信息
    session.setAttribute(SysConsts.SESSION.TEACHER_ID, teacher.getId());
    session.setAttribute(SysConsts.SESSION.TEACHER, teacher);
    // 重定向到教师主页
    return R.successWithData(teacher.getId());
  }

  /**
   * 修改密码
   *
   * @param dto 密码信息
   * @return 重定向登录页面
   */
  @PostMapping("/update/pass")
  public R updatePassword(ChangePassDto dto) {
    // 获取 session 对象
    HttpSession session = HttpContextUtil.getSession();
    // 调用密码修改接口
    teacherService.updatePassword(dto);
    // 移除 session 信息
    session.removeAttribute(SysConsts.SESSION.TEACHER_ID);
    session.removeAttribute(SysConsts.SESSION.TEACHER);
    return R.success();
  }

  @GetMapping("/{id}")
  public Teacher getOne(@PathVariable Integer id) {
    return this.teacherService.getById(id);
  }

  /**
   * 更新教师信息
   *
   * @param teacher 教师信息
   * @return 成功信息
   */
  @PostMapping("/update")
  public R updateTeacher(Teacher teacher) {
    // 通过ID关系教师信息
    this.teacherService.updateById(teacher);
    return R.success();
  }

  /**
   * 增加教师
   *
   * @param teacher 教师信息
   * @return 成功信息
   */
  @PostMapping("/save")
  public R saveTeacher(Teacher teacher) {
    // 调用教师信息新增接口
    this.teacherService.save(teacher);
    return R.success();
  }

  /**
   * 删除教师
   *
   * @param id 教师ID
   * @return 回调信息
   */
  @PostMapping("/delete/{id}")
  public R deleteTeacher(@PathVariable Integer id) {
    this.teacherService.removeById(id);
    return R.success();
  }
}
