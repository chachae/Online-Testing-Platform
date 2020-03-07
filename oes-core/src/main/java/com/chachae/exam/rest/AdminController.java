package com.chachae.exam.rest;

import com.chachae.exam.common.base.R;
import com.chachae.exam.common.constant.SysConsts;
import com.chachae.exam.common.model.Admin;
import com.chachae.exam.common.model.dto.ChangePassDto;
import com.chachae.exam.common.util.HttpContextUtil;
import com.chachae.exam.core.annotation.Permissions;
import com.chachae.exam.service.AdminService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 * @author chachae
 * @since 2020/2/28 15:35
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

  @Resource private AdminService adminService;

  /**
   * 管理员登录<a href="http://localhost:8080/admin/login">管理员登录接口</a>
   *
   * @param adminId 管理员账号
   * @param password 管理员密码
   * @return 管理员信息
   */
  @PostMapping("/login")
  public R login(String adminId, String password) {
    // 获取session 对象
    HttpSession session = HttpContextUtil.getSession();
    // 登陆操作
    Admin admin = this.adminService.login(adminId, password);
    // 设置session
    session.setAttribute(SysConsts.Session.ADMIN, admin);
    session.setAttribute(SysConsts.Session.ROLE_ID, admin.getRoleId());
    // 重定向到管理员主页
    return R.successWithData(admin.getId());
  }

  /**
   * 删除管理员
   *
   * @param id 管理员id
   * @return 删除回调信息
   */
  @PostMapping("/delete/{id}")
  @Permissions("admin:delete")
  public R deleteAdmin(@PathVariable Integer id) {
    // 执行删除
    this.adminService.removeById(id);
    return R.success();
  }

  /**
   * 新增管理员
   *
   * @param admin 管理员信息
   * @return 回调信息
   */
  @PostMapping("/save")
  @Permissions("admin:save")
  public R saveAdmin(Admin admin) {
    // 对于管理员增加接口
    this.adminService.save(admin);
    return R.success();
  }

  /**
   * 提交修改密码信息
   *
   * @param dto 新旧密码信息
   * @return 回调信息
   */
  @PostMapping("/update/pass")
  @Permissions("admin:update:password")
  public R updatePass(ChangePassDto dto) {
    // 调用密码修改接口
    this.adminService.updatePassword(dto);
    // 获取 session 对象后，移除 session 信息
    HttpSession session = HttpContextUtil.getSession();
    session.removeAttribute(SysConsts.Session.TEACHER_ID);
    session.removeAttribute(SysConsts.Session.ROLE_ID);
    session.removeAttribute(SysConsts.Session.TEACHER);
    // 重定向登录页面
    return R.success();
  }
}
