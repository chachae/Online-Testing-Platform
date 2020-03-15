package com.chachae.exam.rest;

import com.chachae.exam.common.base.R;
import com.chachae.exam.core.annotation.Limit;
import com.chachae.exam.util.service.CaptchaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 验证码 rest 控制器
 *
 * @author chachae
 * @since 2020/3/13 11:20
 */
@RestController
@RequestMapping("/api/captcha")
public class CaptchaController {

  @Resource private CaptchaService captchaService;

  @GetMapping
  @Limit(key = "captcha", period = 10, count = 10, name = "验证码接口", prefix = "limit")
  public R get() {
    return R.successWithData(this.captchaService.getCaptcha());
  }
}
