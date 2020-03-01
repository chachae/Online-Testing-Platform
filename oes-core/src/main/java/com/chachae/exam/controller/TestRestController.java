package com.chachae.exam.controller;

import com.chachae.exam.common.base.R;
import com.chachae.exam.core.annotation.Permissions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author chachae
 * @since 2020/3/1 18:09
 */
@RestController
@RequestMapping("/api/rest/test")
public class TestRestController {
  @GetMapping("/yes")
  @Permissions("admin:list")
  public R yes() {
    return R.success();
  }

  @GetMapping("/no")
  @Permissions("teacher:list")
  public R no() {
    return R.success();
  }
}
