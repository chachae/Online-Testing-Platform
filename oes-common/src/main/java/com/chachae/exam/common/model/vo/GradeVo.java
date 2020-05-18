package com.chachae.exam.common.model.vo;

import com.chachae.exam.common.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author chachae
 * @since 2020/5/18 21:47
 */
@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GradeVo extends BaseEntity {

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


  /**
   * 专业名称
   */
  private String majorName;

  /**
   * 班级名称
   */
  private String gradeName;

}
