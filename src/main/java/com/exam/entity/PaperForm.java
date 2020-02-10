package com.exam.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 试卷题目类型信息实体
 *
 * @author yzn
 * @date 2020/1/6
 */
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaperForm extends Model<PaperForm> {
  @TableId(type = IdType.AUTO)
  private Integer id;

  /** 单选题数目 */
  private String qChoiceNum;

  /** 单选题分数 */
  private String qChoiceScore;

  /** 多选题数目 */
  private String qMulChoiceNum;

  /** 多选题分数 */
  private String qMulChoiceScore;

  /** 判断题数目 */
  private String qTofNum;

  /** 判断题分数 */
  private String qTofScore;

  /** 填空题数目 */
  private String qFillNum;

  /** 填空题分数 */
  private String qFillScore;

  /** 简答题数目 */
  private String qSaqNum;

  /** 简答题分数 */
  private String qSaqScore;

  /** 编程题数目 */
  private String qProgramNum;

  /** 编程题分数 */
  private String qProgramScore;
}
