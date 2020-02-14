package com.exam.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * 课程实体类
 *
 * @author yzn
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

  /** 任课老师姓名 */
  private String teacherName;

  /** 该门课的出题老师(默认一门课一个老师出题) */
  private Integer teacherId;
}
