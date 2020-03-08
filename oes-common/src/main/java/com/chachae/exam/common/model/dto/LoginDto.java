package com.chachae.exam.common.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * @author chachae
 * @since 2020/3/8 21:43
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class LoginDto {

  @NotBlank(message = "用户不能为空")
  private String username;

  @NotBlank(message = "密码不能为空")
  @Size(min = 3, message = "密码错误")
  private String password;
}
