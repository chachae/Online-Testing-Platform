package com.exam.controller;

import com.exam.common.Page;
import com.exam.common.R;
import com.exam.constant.SysConsts;
import com.exam.entity.Academy;
import com.exam.entity.Admin;
import com.exam.entity.Announce;
import com.exam.entity.Teacher;
import com.exam.service.AcademyService;
import com.exam.service.AdminService;
import com.exam.service.AnnounceService;
import com.exam.service.TeacherService;
import com.exam.util.HttpContextUtil;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 管理员模块控制层
 *
 * @author yzn
 * @date 2020/2/5
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

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
    return "admin/login";
  }

  /**
   * 管理员登录<a href="http://localhost:8080/admin/login">管理员登录接口</a>
   *
   * @param r 重定向 r 对象
   * @param adminId 管理员账号
   * @param adminPassword 管理员密码
   * @return 管理员信息
   */
  @PostMapping("/login")
  public String login(RedirectAttributes r, String adminId, String adminPassword) {
    // 获取session 对象
    HttpSession session = HttpContextUtil.getSession();
    try {
      // 登陆操作
      Admin admin = this.adminService.login(adminId, adminPassword);
      // 设置session
      session.setAttribute(SysConsts.SESSION.ADMIN, admin);
      session.setAttribute(SysConsts.SESSION.ROLE_ID, admin.getRoleId());
      return "redirect:/admin/home/" + admin.getId();
    } catch (Exception e) {
      r.addFlashAttribute("message", e.getMessage());
      return "redirect:/admin/login";
    }
  }

  /**
   * 管理员主界面
   *
   * @param model model 对象
   * @param id 管理员 ID
   * @return 管理员页面
   */
  @GetMapping("/home/{id}")
  public String home(Model model, @PathVariable Integer id) {
    // 调用ID 查询接口
    Admin admin = this.adminService.getById(id);
    model.addAttribute("admin", admin);
    return "admin/home";
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
    session.removeAttribute(SysConsts.SESSION.ADMIN);
    session.removeAttribute(SysConsts.SESSION.ROLE_ID);
    return "redirect:/admin/login";
  }

  /**
   * 获取全部公告
   *
   * @param model model 对象
   * @return 公告信息
   */
  @GetMapping("/announce")
  public String announceList(Model model) {
    // 调用公告列表查询接口
    List<Announce> announceList = this.announceService.list();
    model.addAttribute("announceList", announceList);
    return "admin/announceList";
  }

  /**
   * 返回公告页面
   *
   * @return /
   */
  @GetMapping("/announce/new")
  public String announceNew() {
    return "admin/newAnnounce";
  }

  /**
   * 新增一个公告
   *
   * @param announce 公告信息
   * @return 公告页面
   */
  @PostMapping("/announce/new")
  public String announceNew(Announce announce) {
    // 调用新增公告接口
    this.announceService.save(announce);
    return "redirect:/admin/announce";
  }

  /**
   * 删除单个公告
   *
   * @param id 公告ID
   * @return 成功信息
   */
  @GetMapping("/announce/del")
  @ResponseBody
  public R delAnnounce(Integer id) {
    // 调用公告删除接口
    this.announceService.removeById(id);
    return R.success();
  }

  /**
   * 批量删除公告
   *
   * @param ids 公告ID集合，<a href="http://localhost:8080//announce/del?ids=1,2,3,4"
   * @return 成功信息
   */
  @PostMapping("/announce/del")
  @ResponseBody
  public R delAnnounces(Integer[] ids) {
    // 调用公告批量删除接口
    this.announceService.delete(ids);
    return R.success();
  }

  /**
   * 教师列表
   *
   * @param page 教室列表
   * @param model model 对象
   * @return 教师列表页面
   */
  @GetMapping("/teacher")
  public String list(Page page, Model model) {
    // 获取学生分页数据
    PageInfo<Teacher> pageInfo = teacherService.pageForTeacherList(page.getPageNo());
    // 设置分页后的数据的 model 对象
    model.addAttribute("page", pageInfo);
    return "admin/teacherList";
  }

  /**
   * 更新教师信息
   *
   * @param teacher 教师信息
   * @return 成功信息
   */
  @ResponseBody
  @PostMapping("/teacher/update")
  public R updateTeacher(Teacher teacher) {
    this.teacherService.updateById(teacher);
    return R.success();
  }

  /**
   * 增加教师
   *
   * @param teacher 教师信息
   * @return 成功信息
   */
  @ResponseBody
  @PostMapping("/teacher/save")
  public R saveTeacher(Teacher teacher) {
    this.teacherService.save(teacher);
    return R.success();
  }

  /**
   * 转发到学院管理页面
   *
   * @param model model 对象
   * @return 学院管理页面
   */
  @GetMapping("/academy")
  public String list(Model model) {
    List<Academy> academyList = this.academyService.list();
    model.addAttribute("academyList", academyList);
    return "/admin/academyList";
  }

  /**
   * 更新学院信息
   *
   * @param academy 教师信息
   * @return 成功信息
   */
  @ResponseBody
  @PostMapping("/academy/update")
  public R updateAcademy(Academy academy) {
    this.academyService.updateById(academy);
    return R.success();
  }

  /**
   * 增加学院
   *
   * @param academy 教师信息
   * @return 成功信息
   */
  @ResponseBody
  @PostMapping("/academy/save")
  public R saveAcademy(Academy academy) {
    this.academyService.save(academy);
    return R.success();
  }
}
