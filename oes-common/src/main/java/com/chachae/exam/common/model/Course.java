package com.chachae.exam.common.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 课程实体类
 *
 * @author chachae
 */
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Course {

  @TableId
  private Integer id;

  @NotBlank(message = "课程名称{required}")
  @Size(max = 20, message = "课程名称{noMoreThan}")
  private String courseName;

  /**
   * 该门课的出题老师(默认一门课一个老师出题)
   */
  private String teacherIds;

  /**
   * 课程所属学院
   */
  @NotNull(message = "所属学院{required}")
  private Integer academyId;

  /**
   * 教师工号列表
   */
  @TableField(exist = false)
  @NotBlank(message = "教师工号{required}")
  private String workNumbers;

  /**
   * 教师姓名列表
   */
  @TableField(exist = false)
  @NotBlank(message = "教师姓名{required}")
  private String names;

  /**
   * 教师工号列表
   */
  @TableField(exist = false)
  private Academy academy;
}
