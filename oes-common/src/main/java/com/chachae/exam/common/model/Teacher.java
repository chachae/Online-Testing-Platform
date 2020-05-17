package com.chachae.exam.common.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.chachae.exam.common.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.validation.constraints.NotBlank;
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
@EqualsAndHashCode(callSuper = true)
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
  @NotBlank(message = "姓名{required}")
  private String name;

  /**
   * 工号
   */
  @NotBlank(message = "工号{required}")
  private String workNumber;

  /**
   * 密码
   */
  @JsonIgnore
  @NotBlank(message = "密码{required}")
  private String password;

  /**
   * 角色id
   */
  private Integer roleId;

  /**
   * 职位
   */
  @NotBlank(message = "职位{required}")
  private String job;

  /**
   * 性别
   */
  private String sex;

  /**
   * 所属学院
   */
  @NotNull(message = "学院{required}")
  private Integer academyId;

  /**
   * 学院信息
   */
  @TableField(exist = false)
  @JsonIgnore
  private Academy academy;
}
