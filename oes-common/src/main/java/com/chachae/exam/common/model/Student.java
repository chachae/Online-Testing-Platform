package com.chachae.exam.common.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.chachae.exam.common.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
  @NotBlank(message = "姓名{required}")
  @Size(min = 1, max = 30, message = "姓名{range}")
  private String name;

  /**
   * 学生登录密码
   */
  @JsonIgnore
  @NotBlank(message = "登录密码{required}")
  private String password;

  /**
   * 学号
   */
  @NotBlank(message = "学号{required}")
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
  @NotNull(message = "专业{required}")
  private Integer majorId;

  /**
   * 年级
   */
  @NotNull(message = "年级{required}")
  private Integer level;
}
