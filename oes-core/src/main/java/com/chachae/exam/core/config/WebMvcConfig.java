package com.chachae.exam.core.config;

import com.chachae.exam.core.interceptor.LoginInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WebMvc 配置
 *
 * @author yzn
 * @date 2020/1/31
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

  /**
   * 登录拦截器注入
   */
  private final LoginInterceptor loginInterceptor;

  /**
   * 拦截器注册器
   */
  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    // 注册登录拦截器
    registry.addInterceptor(loginInterceptor).addPathPatterns("/**");
  }
}
