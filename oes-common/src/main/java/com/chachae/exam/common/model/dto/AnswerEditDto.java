package com.chachae.exam.common.model.dto;

import com.chachae.exam.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 修改主观题成绩
 *
 * @author chachae
 * @since 2020/2/7 21:42
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class AnswerEditDto extends BaseEntity {

  // 答题记录编号 id
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
