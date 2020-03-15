package com.chachae.exam.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * The type Index controller.
 *
 * @author chachae
 * @since 2020 /2/26 15:14
 */
@Controller
public class LoginModuleController {

  /**
   * Login string.
   *
   * @return the string
   */
  @GetMapping({"/", "/login"})
  public String login() {
    return "/auth/login";
  }
}
