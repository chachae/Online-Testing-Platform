package com.exam.entity.dto;

import com.exam.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 修改主观题成绩
 *
 * @author yzn
 * @since 2020/2/7 21:42
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class AnswerEditDto extends BaseEntity {

  // 题目id
  private Integer id;
  // 旧的成绩
  private Integer oldScore;
  // 新的成绩
  private Integer newScore;
  // 试卷id
  private Integer paperId;
  // 学生id
  private Integer stuId;
}
