package com.chachae.exam.controller.common;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author chachae
 * @since 2020/2/26 15:14
 */
@Controller
public class IndexController {

  /**
   * 转发到登录界面
   *
   * @return 学生登录界面
   */
  @GetMapping({"/", "/login"})
  public String login() {
    return "/auth/login";
  }
}
