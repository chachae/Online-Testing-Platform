package com.chachae.exam.common.entity.dto;

import com.chachae.exam.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 学生答题记录bean
 *
 * @author chachae
 * @since 2020/2/7 21:20
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class StudentAnswerDto extends BaseEntity {

  /** 题目ID */
  private Integer id;

  /** 题目内容 */
  private String questionName;

  /** 学生回答内容 */
  private String answer;

  /** 学生得分 */
  private Integer score;

  private Integer questionId;
}
