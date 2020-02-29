package com.exam.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.exam.constant.SysConsts;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * 试卷实体类
 *
 * @author yzn
 * @date 2020/1/18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Paper extends Model<Paper> {
  @TableId(type = IdType.AUTO)
  private Integer id;

  /** 试卷名称 */
  private String paperName;

  /** 课程id */
  private Integer courseId;

  /** 问题id组合 */
  private String questionId;

  /** 试卷开始时间 */
  private String beginTime;

  /** 试卷结束时间 */
  private String endTime;

  /** 考试时长 */
  private String allowTime;

  /** 试卷总分 */
  private String score;

  /** 考试状态：未开始，已结束 */
  private String paperState;

  /** 试卷类型：正式，模拟 */
  private String paperType;

  /** 专业班级id */
  private Integer majorId;

  /** 试卷组成id */
  private Integer paperFormId;

  /** 出卷老师id */
  private Integer teacherId;

  /**
   * 判断开始时间是否在当前时间之后
   *
   * @return true 是的 试卷已经开始 false 否 试卷未开始
   */
  @JsonIgnore
  public boolean isStart() {
    if (getPaperType().equals(SysConsts.PAPER.PAPER_TYPE_FORMAL)) {
      final DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");
      DateTime beginTime = fmt.parseDateTime(getBeginTime());
      return beginTime.isBeforeNow();
    } else {
      return true;
    }
  }

  /**
   * 判断结束时间是否在当前时间之后
   *
   * @return true 是的 试卷已经结束 false 否 试卷未结束
   */
  @JsonIgnore
  public boolean isEnd() {
    if (getPaperType().equals(SysConsts.PAPER.PAPER_TYPE_FORMAL)) {
      DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");
      DateTime endTime = formatter.parseDateTime(getEndTime());
      return endTime.isBeforeNow();
    } else {
      return true;
    }
  }
}
