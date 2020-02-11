package com.exam.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.*;

/**
 * 学生实体类
 *
 * @author yzn
 * @date 2020/2/5
 */
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Student extends Model<Student> {
  @TableId(type = IdType.AUTO)
  private Integer id;

  /** 学生姓名 */
  private String name;

  /** 学生登录密码 */
  private String password;

  /** 学号 */
  private String stuNumber;

  /** 角色id */
  private Integer roleId;

  /** 性别 */
  private String sex;

  /** 专业id */
  private Integer majorId;

  /** 年级 */
  private Integer level;
}
