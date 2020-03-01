package com.chachae.exam.controller;

import com.chachae.exam.core.annotation.Permissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author chachae
 * @since 2020/3/1 18:09
 */
@Controller
@RequestMapping("/test")
public class TestController {

  @GetMapping("/no")
  @Permissions("admin:list")
  public String no() {
    return "123";
  }
}
