package com.chachae.exam.core.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @author chachae
 * @since 2020/3/1 22:37
 */
@Data
@Component
@PropertySource(value = {"classpath:application.properties"})
@ConfigurationProperties(prefix = "oes")
public class Props {

  private SysProperties sys = new SysProperties();
}
