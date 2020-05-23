package com.chachae.exam.common.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 分数实体
 *
 * @author chachae
 * @date 2020/1/5
 */
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Score {

  @TableId
  private Integer id;

  /**
   * 考生id
   */
  private Integer stuId;

  /**
   * 试卷id
   */
  private Integer paperId;

  /**
   * 试卷名称
   */
  private String paperName;

  /**
   * 试卷分数
   */
  private String score;

  /**
   * 错题id集合
   */
  private String wrongIds;

  @JsonIgnore
  @TableField(exist = false)
  private Paper paper;

  @JsonIgnore
  @TableField(exist = false)
  private Integer paperAvgScore;

  @JsonIgnore
  @TableField(exist = false)
  private Integer sort;

  public Score(Integer stuId, Integer paperId, String paperName, String score, String wrongIds) {
    this.stuId = stuId;
    this.paperId = paperId;
    this.paperName = paperName;
    this.score = score;
    this.wrongIds = wrongIds;
  }
}
