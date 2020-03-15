package com.chachae.exam.util.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.chachae.exam.common.exception.ServiceException;
import com.chachae.exam.util.model.Captcha;
import com.chachae.exam.util.service.CaptchaService;
import com.wf.captcha.ArithmeticCaptcha;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author chachae
 * @since 2020/3/13 11:26
 */
@Service
public class CaptchaServiceImpl implements CaptchaService {

  @Resource private RedisTemplate<String, Object> redisTemplate;

  @Value("${oes.cache.captcha_prefix}")
  private String prefix;

  @Value("${oes.cache.captcha_expire}")
  private Long expire;

  @Override
  public Captcha getCaptcha() {
    // 算术类型 <a href="https://gitee.com/whvse/EasyCaptcha">
    ArithmeticCaptcha captcha = new ArithmeticCaptcha(90, 24);
    // 生成唯一key
    String key = prefix + IdUtil.fastSimpleUUID();
    // 存入redis 中，时间3分钟
    redisTemplate.opsForValue().set(key, captcha.text(), expire, TimeUnit.SECONDS);
    return Captcha.builder().key(key).base64(captcha.toBase64()).build();
  }

  @Override
  public void validate(Captcha captcha) {
    Object value = redisTemplate.opsForValue().get(captcha.getKey());
    // 移除redis 中的验证码
    redisTemplate.delete(captcha.getKey());
    if (ObjectUtil.isEmpty(value)) {
      throw new ServiceException("验证码过期");
    }
    if (!String.valueOf(value).equals(captcha.getText())) {
      throw new ServiceException("验证码错误");
    }
  }
}
