package com.chachae.exam.common.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.chachae.exam.common.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 班级表实体类
 *
 * @author chachae
 * @since 2020-05-18 21:26:46
 */
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Grade extends BaseEntity {

  @TableId
  private Integer id;
  /**
   * 班级年级
   */
  private Integer level;
  /**
   * 所属专业id
   */
  private Integer majorId;
  /**
   * 班级编号
   */
  private Integer gradeNumber;

}