package com.chachae.exam.common.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.chachae.exam.common.constant.SysConsts;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


/**
 * 试卷实体类
 *
 * @author chachae
 * @date 2020/1/18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Paper {

  @TableId
  private Integer id;

  /**
   * 试卷名称
   */
  @NotBlank(message = "试卷名称{required}")
  private String paperName;

  /**
   * 课程id
   */
  @NotNull(message = "课程{required}")
  private Integer courseId;

  /**
   * 问题id组合
   */
  private String questionId;

  /**
   * 试卷开始时间
   */
  private String beginTime;

  /**
   * 试卷结束时间
   */
  private String endTime;

  /**
   * 考试时长
   */
  private String allowTime;

  /**
   * 试卷总分
   */
  @NotBlank(message = "试卷总分{required}")
  private String score;

  /**
   * 考试状态：未开始，已结束
   */
  private String paperState;

  /**
   * 试卷类型：正式，模拟
   */
  private String paperType;

  /**
   * 专业班级id
   */
  @NotNull(message = "专业{required}")
  private Integer majorId;

  /**
   * 试卷组成id
   */
  private Integer paperFormId;

  /**
   * 出卷老师id
   */
  private Integer teacherId;

  /**
   * 试卷所属学院
   */
  private Integer academyId;

  /**
   * 试卷所属班级集合
   */
  private String gradeIds;

  /**
   * 试卷年级
   */
  private Integer level;

  /**
   * 判断开始时间是否在当前时间之后
   *
   * @return true 是的 试卷已经开始 false 否 试卷未开始
   */
  public boolean isStart() {
    if (getPaperType().equals(SysConsts.Paper.PAPER_TYPE_FORMAL)) {
      LocalDateTime ldt = LocalDateTime
          .parse(getBeginTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
      return ldt.isBefore(LocalDateTime.now());
    } else {
      return true;
    }
  }

  /**
   * 判断结束时间是否在当前时间之后
   *
   * @return true 是的 试卷已经结束 false 否 试卷未结束
   */
  public boolean isEnd() {
    if (getPaperType().equals(SysConsts.Paper.PAPER_TYPE_FORMAL)) {
      LocalDateTime ldt = LocalDateTime
          .parse(getEndTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
      return ldt.isBefore(LocalDateTime.now());
    } else {
      return true;
    }
  }
}
