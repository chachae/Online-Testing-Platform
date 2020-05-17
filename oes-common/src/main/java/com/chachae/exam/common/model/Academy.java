package com.chachae.exam.common.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.chachae.exam.common.base.BaseEntity;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 学院表实体类
 *
 * @author chachae
 * @since 2020-02-09 12:09:59
 */
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
public class Academy extends BaseEntity {

  /**
   * 学院id
   */
  @TableId
  private Integer id;

  /**
   * 学院名称
   */
  @NotBlank(message = "学院名称{required}")
  @Size(min = 2, max = 30, message = "学院名称{range}")
  private String name;

}
