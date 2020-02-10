package com.exam.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * 试卷实体类
 *
 * @author yzn
 * @date 2020/1/18
 */
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Data
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
  public boolean isStart() {
    final DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");
    DateTime beginTime = fmt.parseDateTime(getBeginTime());
    return beginTime.isBeforeNow();
  }

  /**
   * 判断结束时间是否在当前时间之后
   *
   * @return true 是的 试卷已经结束 false 否 试卷未结束
   */
  public boolean isEnd() {
    final DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");
    DateTime endTime = fmt.parseDateTime(getEndTime());
    return endTime.isBeforeNow();
  }
}
