package com.chachae.exam.common.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.chachae.exam.common.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 教师实体类
 *
 * @author chachae
 * @date 2020/1/18
 */
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Teacher extends BaseEntity {

  @TableId
  private Integer id;

  /**
   * 姓名
   */
  private String name;

  /**
   * 工号
   */
  private String workNumber;

  /**
   * 密码
   */
  private String password;

  /**
   * 角色id
   */
  private Integer roleId;

  /**
   * 职位
   */
  private String job;

  /**
   * 性别
   */
  public String sex;

}
