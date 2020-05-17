package com.chachae.exam.common.model.dto;

import javax.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author chachae
 * @since 2020/3/8 21:43
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class LoginDto {

  @NotBlank(message = "用户名{required}")
  private String username;

  @NotBlank(message = "密码{required}")
  private String password;
}
