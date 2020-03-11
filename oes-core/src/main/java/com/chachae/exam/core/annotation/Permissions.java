package com.chachae.exam.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 接口权限校验注解
 *
 * @author chachae
 * @since 2020/3/1 17:30
 */

// 元注解，定义注解被保留策略，一般有三种策略
// 1、RetentionPolicy.SOURCE 注解只保留在源文件中，在编译成class文件的时候被遗弃
// 2、RetentionPolicy.CLASS 注解被保留在class中，但是在jvm加载的时候北欧抛弃，这个是默认的声明周期
// 3、RetentionPolicy.RUNTIME 注解在jvm加载的时候仍被保留
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Permissions {

  // 权限表达式
  String value() default "";
}
