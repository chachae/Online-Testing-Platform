package com.exam.entity.dto;

import com.exam.entity.StuAnswerRecord;

import java.util.List;

/**
 * 评分信息实体类
 *
 * @author yzn
 * @since 2020/2/6 21:46
 */
public class MarkInfoDto {

  private Integer score;

  private List<String> wrongIds;

  private List<StuAnswerRecord> stuAnswerRecord;

  public List<StuAnswerRecord> getStuAnswerRecord() {
    return stuAnswerRecord;
  }

  public void setStuAnswerRecord(List<StuAnswerRecord> stuAnswerRecord) {
    this.stuAnswerRecord = stuAnswerRecord;
  }

  public Integer getScore() {
    return score;
  }

  public void setScore(Integer score) {
    this.score = score;
  }

  public List<String> getWrongIds() {
    return wrongIds;
  }

  public void setWrongIds(List<String> wrongIds) {
    this.wrongIds = wrongIds;
  }
}
