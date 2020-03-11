package com.chachae.exam.core.annotation;

import com.chachae.exam.common.base.LimitType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 接口限流注解
 *
 * @author chachae
 * @since 2020/3/11
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Limit {

  /** 资源名称，用于描述接口功能 */
  String name() default "";

  /** 资源 key */
  String key() default "";

  /** key prefix */
  String prefix() default "";

  /** 时间范围，单位秒 */
  int period();

  /** 限制访问次数 */
  int count();

  /** 限制类型 */
  LimitType limitType() default LimitType.CUSTOMER;
}
