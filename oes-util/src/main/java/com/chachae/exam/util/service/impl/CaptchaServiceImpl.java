package com.chachae.exam.util.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.chachae.exam.common.exception.ServiceException;
import com.chachae.exam.util.model.Captcha;
import com.chachae.exam.util.service.CaptchaService;
import com.wf.captcha.ArithmeticCaptcha;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 验证码实现
 *
 * @author chachae
 * @since 2020/3/13 11:26
 */
@Service
@RequiredArgsConstructor
public class CaptchaServiceImpl implements CaptchaService {

  private final RedisTemplate<String, Object> redisTemplate;

  @Value("${oes.cache.captcha_prefix}")
  private String prefix;

  @Value("${oes.cache.captcha_expire}")
  private Long expire;

  @Override
  public Captcha getCaptcha() {
    // 算术类型 <a href="https://gitee.com/whvse/EasyCaptcha">
    ArithmeticCaptcha captcha = new ArithmeticCaptcha(90, 24);
    String key = prefix + IdUtil.fastSimpleUUID();
    redisTemplate.opsForValue().set(key, captcha.text(), expire, TimeUnit.SECONDS);
    return Captcha.builder().key(key).base64(captcha.toBase64()).build();
  }

  @Override
  public void validate(Captcha captcha) {
    String redisValue = (String) redisTemplate.opsForValue().get(captcha.getKey());
    if (StrUtil.isBlank(redisValue)) {
      throw new ServiceException("验证码过期");
    }
    redisTemplate.delete(captcha.getKey());
    if (!captcha.getText().equalsIgnoreCase(redisValue)) {
      throw new ServiceException("验证码错误");
    }
  }
}
