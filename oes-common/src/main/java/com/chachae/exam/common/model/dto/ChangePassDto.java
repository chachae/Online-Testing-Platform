package com.chachae.exam.common.model.dto;

import com.chachae.exam.common.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 通用修改密码 JavaBean
 *
 * @author chachae
 * @since 2020/2/10 14:43
 */
@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePassDto extends BaseEntity {

  @NotNull(message = "用户 ID 不能为空")
  private Integer id;

  @NotBlank(message = "原密码不能为空")
  private String oldPassword;

  @NotBlank(message = "新密码不能为空")
  private String password;
}
