package com.chachae.exam;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
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
@MapperScan("com.chachae.exam.common.mapper")
public class OesApplication {

  public static void main(String[] args) {
    SpringApplication.run(OesApplication.class, args);
  }
}
