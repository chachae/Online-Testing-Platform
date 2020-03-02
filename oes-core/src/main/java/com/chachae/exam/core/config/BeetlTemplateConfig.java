package com.chachae.exam.core.config;

import org.beetl.core.resource.ClasspathResourceLoader;
import org.beetl.ext.spring.BeetlGroupUtilConfiguration;
import org.beetl.ext.spring.BeetlSpringViewResolver;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

/**
 * Beetl 配置
 *
 * @author chachae
 * @since 2020/2/26 16:10
 */
@Configuration
public class BeetlTemplateConfig {

  @Bean(name = "beetlConfig")
  public BeetlGroupUtilConfiguration getBeetlGroupUtilConfiguration() {
    BeetlGroupUtilConfiguration beetlGroupUtilConfiguration = new BeetlGroupUtilConfiguration();
    ClasspathResourceLoader classpathResourceLoader = new ClasspathResourceLoader();
    ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
    Resource beetlResource = resolver.getResource("classpath:beetl.properties");
    beetlGroupUtilConfiguration.setConfigFileResource(beetlResource);
    beetlGroupUtilConfiguration.setResourceLoader(classpathResourceLoader);
    beetlGroupUtilConfiguration.init();
    return beetlGroupUtilConfiguration;
  }

  @Bean(name = "beetlViewResolver")
  public BeetlSpringViewResolver getBeetlSpringViewResolver(
      @Qualifier("beetlConfig") BeetlGroupUtilConfiguration beetlGroupUtilConfiguration) {
    BeetlSpringViewResolver beetlSpringViewResolver = new BeetlSpringViewResolver();
    beetlSpringViewResolver.setSuffix(".html");
    beetlSpringViewResolver.setContentType("text/html;charset=UTF-8");
    beetlSpringViewResolver.setOrder(0);
    beetlSpringViewResolver.setCache(true);
    beetlSpringViewResolver.setConfig(beetlGroupUtilConfiguration);
    return beetlSpringViewResolver;
  }
}
