package com.chachae.exam.common.model.dto;

import com.chachae.exam.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author chachae
 * @since 2020/5/16 23:02
 */

@EqualsAndHashCode(callSuper = false)
@Data
public class StudentExcelDto extends BaseEntity {

  /**
   * 学号
   */
  private String stuNumber;

  /**
   * 专业统一代码
   */
  private Integer majorId;

  /**
   * 性别
   */
  private Integer sex;

  /**
   * 姓名
   */
  private String name;

  /**
   * 学生年级
   */
  private Integer level;
}
