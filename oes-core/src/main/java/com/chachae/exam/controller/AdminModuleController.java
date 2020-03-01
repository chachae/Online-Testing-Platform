package com.chachae.exam.controller;

import com.chachae.exam.common.base.Page;
import com.chachae.exam.common.constant.SysConsts;
import com.chachae.exam.common.util.HttpContextUtil;
import com.chachae.exam.service.AcademyService;
import com.chachae.exam.service.AdminService;
import com.chachae.exam.service.AnnounceService;
import com.chachae.exam.service.TeacherService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
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

  @Resource private AdminService adminService;
  @Resource private TeacherService teacherService;
  @Resource private AcademyService academyService;
  @Resource private AnnounceService announceService;

  /**
   * 管理员登录页面
   *
   * @return 管理员登录页面
   */
  @GetMapping("/login")
  public String login() {
    return "/admin/self/login";
  }

  /**
   * 管理员主界面
   *
   * @param id 管理员 ID
   * @return 管理员页面
   */
  @GetMapping("/home/{id}")
  public ModelAndView home(ModelAndView mv, @PathVariable Integer id) {
    // 调用ID 查询接口
    // 设置管理员的 model 对象信息
    mv.addObject("admin", this.adminService.getById(id));
    mv.setViewName("/admin/self/home");
    return mv;
  }

  /**
   * 修改密码
   *
   * @return 修改密码界面
   */
  @GetMapping("{id}/changePass")
  public String toChangPass() {
    return "/admin/self/update-pass";
  }

  /**
   * 管理员退出
   *
   * @return 管理员登录页面
   */
  @GetMapping("/logout")
  public String logout() {
    // 获取session 对象
    HttpSession session = HttpContextUtil.getSession();
    // 删除当前用户的session
    session.removeAttribute(SysConsts.Session.ADMIN);
    session.removeAttribute(SysConsts.Session.ROLE_ID);
    return "redirect:/admin/login";
  }

  /**
   * 获取全部公告
   *
   * @return 公告信息
   */
  @GetMapping("/announce")
  public ModelAndView announceList(ModelAndView mv) {
    mv.addObject("announceList", this.announceService.list());
    mv.setViewName("/admin/announce/list");
    return mv;
  }

  /**
   * 教师列表
   *
   * @param page 教室列表
   * @param mv ModelAndView 对象
   * @return 教师列表页面
   */
  @GetMapping("/teacher")
  public ModelAndView list(Page page, ModelAndView mv) {
    // 设置分页后的数据的 model 对象
    mv.addObject("page", teacherService.pageForTeacherList(page.getPageNo()));
    mv.setViewName("/admin/teacher/list");
    return mv;
  }

  /**
   * 转发到学院管理页面
   *
   * @param mv ModelAndView 对象
   * @return 学院管理页面
   */
  @GetMapping("/academy")
  public ModelAndView list(ModelAndView mv) {
    // 调用学院集合
    mv.addObject("academyList", this.academyService.list());
    mv.setViewName("/admin/academy/list");
    return mv;
  }

  /**
   * 获取管理员分页信息
   *
   * @param mv ModelAndView 对象
   * @return 分页结果集
   */
  @GetMapping
  public ModelAndView listAdmin(Page page, ModelAndView mv) {
    // 获取试卷分页信息
    mv.addObject("page", this.adminService.pageForAdminList(page.getPageNo()));
    mv.setViewName("/admin/admin/list");
    return mv;
  }
}
