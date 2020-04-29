package com.chachae.exam.rest;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chachae.exam.common.base.R;
import com.chachae.exam.common.constant.SysConsts;
import com.chachae.exam.common.model.Admin;
import com.chachae.exam.common.model.dto.ChangePassDto;
import com.chachae.exam.common.model.dto.LoginDto;
import com.chachae.exam.common.util.HttpUtil;
import com.chachae.exam.core.annotation.Limit;
import com.chachae.exam.core.annotation.Permissions;
import com.chachae.exam.service.AdminService;
import com.chachae.exam.util.model.Captcha;
import com.chachae.exam.util.service.CaptchaService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Map;

/**
 * @author chachae
 * @since 2020/2/28 15:35
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

  @Resource private AdminService adminService;
  @Resource private CaptchaService captchaService;

  /**
   * 管理员登录<a href="http://localhost:8080/login>管理员登录接口</a>
   *
   * @param entity 账号密码
   * @return 管理员信息
   */
  @PostMapping("/login")
  @Limit(key = "adminLogin", period = 60, count = 8, name = "管理员登录接口", prefix = "limit")
  public R login(@Valid LoginDto entity, @Valid Captcha captcha) {
    // 验证码验证
    this.captchaService.validate(captcha);
    // 登陆操作
    Admin admin = this.adminService.login(entity.getUsername(), entity.getPassword());
    // 设置session
    HttpSession session = HttpUtil.getSession();
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
  public R updatePass(@Valid ChangePassDto dto) {
    // 调用密码修改接口
    this.adminService.updatePassword(dto);
    // 获取 session 对象后，移除 session 信息
    HttpSession session = HttpUtil.getSession();
    session.removeAttribute(SysConsts.Session.TEACHER_ID);
    session.removeAttribute(SysConsts.Session.ROLE_ID);
    session.removeAttribute(SysConsts.Session.TEACHER);
    // 重定向登录页面
    return R.success();
  }

  /**
   * 分页获取管理员信息
   *
   * @param page 分页信息
   * @return 分页结果集
   */
  @GetMapping("/list")
  @Permissions("admin:list")
  public Map<String, Object> page(Page<Admin> page) {
    return this.adminService.listPage(page);
  }
}
