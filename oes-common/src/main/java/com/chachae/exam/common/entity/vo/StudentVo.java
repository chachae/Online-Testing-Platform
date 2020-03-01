package com.chachae.exam.common.entity.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.chachae.exam.common.base.BaseEntity;
import com.chachae.exam.common.entity.Academy;
import com.chachae.exam.common.entity.Major;
import com.chachae.exam.common.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 学生详细信息实体
 *
 * @author yzn
 * @since 2020/2/8 21:18
 */
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class StudentVo extends BaseEntity {
  @TableId(type = IdType.AUTO)
  private Integer id;

  /** 学生姓名 */
  private String name;

  /** 学号 */
  private String stuNumber;

  /** 角色id */
  private Integer roleId;

  /** 性别 */
  private String sex;

  /** 专业id */
  private Integer majorId;

  /** 专业信息 */
  private Major major;

  /** 年级 */
  private Integer level;

  /** 角色信息 */
  private Role role;

  /** 学院信息 */
  private Academy academy;
}
