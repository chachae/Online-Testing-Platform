package com.chachae.exam.common.model.vo;

import com.chachae.exam.common.base.BaseEntity;
import com.chachae.exam.common.model.Academy;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 专业 vo 对象
 *
 * @author chachae
 * @since 2020/2/9 12:20
 */
@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MajorVo extends BaseEntity {

  private Integer id;

  /** 专业班级 */
  private String major;

  /** 学院 */
  private Integer academyId;

  private Academy academy;
}
