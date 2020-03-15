package com.chachae.exam.util.model;

import com.chachae.exam.common.base.BaseEntity;
import lombok.*;

import javax.validation.constraints.NotNull;

/**
 * 验证码实体类
 *
 * @author chachae
 * @since 2020/3/13 11:27
 */
@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Captcha extends BaseEntity {

  @NotNull(message = "验证码信息异常")
  private String key;

  private String base64;

  @NotNull(message = "请输入验证码")
  private String text;
}
