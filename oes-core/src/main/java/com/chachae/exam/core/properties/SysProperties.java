package com.chachae.exam.core.properties;

import lombok.Data;

/**
 * @author chachae
 * @since 2020/3/1 22:40
 */
@Data
public class SysProperties {

  private String anonUrl;

  private String logoutUrl;

  private String indexUrl;

  private String unauthorizedUrl;
}
