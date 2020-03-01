package com.chachae.exam.common.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 学生主观题答题记录实体类
 *
 * @author chachae
 * @date 2020/2/1
 */
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Data
public class StuAnswerRecord extends Model<StuAnswerRecord> {
  @TableId(type = IdType.AUTO)
  private Integer id;

  /** 试卷id */
  private Integer paperId;

  private Integer stuId;

  /** 题目id */
  private Integer questionId;

  /** 题目答案 */
  private String answer;

  /** 题目得分 */
  private Integer score;
}
