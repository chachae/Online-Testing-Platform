package com.chachae.exam.common.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.chachae.exam.common.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Builder
@Data
public class Teacher extends BaseEntity {

  @TableId
  private Integer id;

  /**
   * 姓名
   */
  @NotNull(message = "姓名不能为空")
  private String name;

  /**
   * 工号
   */
  @NotNull(message = "工号不能为空")
  private String workNumber;

  /**
   * 密码
   */
  @JsonIgnore
  @NotNull(message = "密码不能为空")
  private String password;

  /**
   * 角色id
   */
  private Integer roleId;

  /**
   * 职位
   */
  @NotNull(message = "职位不能为空")
  private String job;

  /**
   * 性别
   */
  @NotNull(message = "性别不能为空")
  private String sex;

  /**
   * 所属学院
   */
  @NotNull(message = "学院不能为空")
  private Integer academyId;

  /**
   * 学院信息
   */
  @TableField(exist = false)
  @JsonIgnore
  private Academy academy;
}
