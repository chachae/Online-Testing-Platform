package com.chachae.exam.common.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.chachae.exam.common.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 学生实体类
 *
 * @author chachae
 * @date 2020/2/5
 */
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Student extends BaseEntity {

  @TableId
  private Integer id;

  /**
   * 学生姓名
   */
  private String name;

  /**
   * 学生登录密码
   */
  @JsonIgnore
  private String password;

  /**
   * 学号
   */
  private String stuNumber;

  /**
   * 角色id
   */
  private Integer roleId;

  /**
   * 性别
   */
  private String sex;

  /**
   * 专业id
   */
  private Integer majorId;

  /**
   * 年级
   */
  private Integer level;
}
