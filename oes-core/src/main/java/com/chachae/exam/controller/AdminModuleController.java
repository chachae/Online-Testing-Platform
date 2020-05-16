package com.chachae.exam.controller;

import com.chachae.exam.common.constant.SysConsts;
import com.chachae.exam.common.util.HttpUtil;
import com.chachae.exam.common.util.ServletUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

/**
 * 管理员模块控制层
 *
 * @author chachae
 * @date 2020/2/5
 */
@Controller
@RequestMapping("/admin")
public class AdminModuleController {

  /**
   * 管理员主界面
   *
   * @return 管理员页面
   */
  @GetMapping("/index")
  public String index() {
    return "/admin/main/index";
  }

  /**
   * 管理员主界面
   *
   * @return 管理员页面
   */
  @GetMapping("/home")
  public String home() {
    return ServletUtil.isAjax() ? "/admin/self/home#homeTable" : "/admin/self/home";
  }

  /**
   * 修改密码
   *
   * @return 修改密码界面
   */
  @GetMapping("/update/pass")
  public String toChangPass() {
    return ServletUtil.isAjax() ? "/admin/self/update-pass#updateTable" : "/admin/self/update-pass";
  }

  /**
   * 展示所教课程
   *
   * @return 课程信息
   */
  @GetMapping("/course")
  public String courseList() {
    return ServletUtil.isAjax() ? "/admin/course/list#courseTable" : "/admin/course/list";
  }

  /**
   * 专业管理页面
   *
   * @return 专业管理页面
   */
  @GetMapping("/major")
  public String listMajor() {
    return ServletUtil.isAjax() ? "/admin/major/list#majorTable" : "/admin/major/list";
  }

  /**
   * 学生分页查询
   *
   * @return 分页结果集
   */
  @GetMapping("/student")
  public String listStudent() {
    return ServletUtil.isAjax() ? "/admin/student/list#studentTable" : "/admin/student/list";
  }

  /**
   * 获取全部公告
   *
   * @return 公告信息
   */
  @GetMapping("/announce")
  public String announceList() {
    return ServletUtil.isAjax() ? "/admin/announce/list#announceTable" : "/admin/announce/list";
  }

  /**
   * 教师列表
   *
   * @return 教师列表页面
   */
  @GetMapping("/teacher")
  public String list() {
    return ServletUtil.isAjax() ? "/admin/teacher/list#teacherTable" : "/admin/teacher/list";
  }

  /**
   * 转发到学院管理页面
   *
   * @return 学院管理页面
   */
  @GetMapping("/academy")
  public String listAcademy() {
    return ServletUtil.isAjax() ? "/admin/academy/list#academyTable" : "/admin/academy/list";
  }

  /**
   * 获取管理员分页信息
   *
   * @return 分页结果集
   */
  @GetMapping
  public String listAdmin() {
    return ServletUtil.isAjax() ? "/admin/admin/list#adminTable" : "/admin/admin/list";
  }

  /**
   * 管理员退出
   *
   * @return 管理员登录页面
   */
  @GetMapping("/logout")
  public String logout() {
    // 获取session 对象
    HttpSession session = HttpUtil.getSession();
    // 删除当前用户的session
    session.removeAttribute(SysConsts.Session.ADMIN);
    session.removeAttribute(SysConsts.Session.ROLE_ID);
    return "redirect:/";
  }
}
