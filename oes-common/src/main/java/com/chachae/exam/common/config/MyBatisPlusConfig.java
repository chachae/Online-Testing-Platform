package com.chachae.exam.common.config;

import com.baomidou.mybatisplus.core.parser.ISqlParser;
import com.baomidou.mybatisplus.extension.parsers.BlockAttackSqlParser;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatisPlus 相关配置
 *
 * @author chachae
 * @date 2019/12/16 19:30
 */
@Configuration
public class MyBatisPlusConfig {

  /**
   * 分页插件配置
   *
   * @return 分页插件对象
   */
  @Bean
  public PaginationInterceptor paginationInterceptor() {
    PaginationInterceptor interceptor = new PaginationInterceptor();
    List<ISqlParser> sqlParserList = new ArrayList<>();
    // 攻击 SQL 阻断解析器、加入解析链
    sqlParserList.add(new BlockAttackSqlParser());
    interceptor.setSqlParserList(sqlParserList);
    return interceptor;
  }
}
