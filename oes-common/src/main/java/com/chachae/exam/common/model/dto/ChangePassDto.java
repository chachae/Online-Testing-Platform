package com.chachae.exam.common.model.dto;

import com.chachae.exam.common.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 修改密码bean
 *
 * @author chachae
 * @since 2020/2/10 14:43
 */
@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePassDto extends BaseEntity {

  private Integer id;

  private String oldPassword;

  private String password;
}
