package com.chachae.exam.common.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.*;

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

  private String courseName;

  /** 该门课的出题老师(默认一门课一个老师出题) */
  private Integer teacherId;

  @TableField(exist = false)
  private Teacher teacher;
}
