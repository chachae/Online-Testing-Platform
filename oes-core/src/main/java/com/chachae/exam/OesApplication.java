package com.chachae.exam;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 项目启动器
 *
 * @author chachae
 * @date 2019/12/31
 */
@EnableAsync
@EnableScheduling
@SpringBootApplication
@EnableTransactionManagement
@MapperScan("com.chachae.exam.common.dao")
public class OesApplication {

  public static void main(String[] args) {
    new SpringApplicationBuilder(OesApplication.class)
        .web(WebApplicationType.SERVLET)
        .run(args);
  }
}
