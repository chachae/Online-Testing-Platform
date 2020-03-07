package com.chachae.exam.common.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
public class Course extends Model<Course> {
  @TableId(type = IdType.AUTO)
  private Integer id;

  @NotBlank(message = "课程名称不能为空")
  @Size(max = 20, message = "课程名称不能超过20个字")
  private String courseName;

  /** 该门课的出题老师(默认一门课一个老师出题) */
  @NotNull(message = "教师 ID 不能为空")
  private Integer teacherId;

  @TableField(exist = false)
  private Teacher teacher;
}
