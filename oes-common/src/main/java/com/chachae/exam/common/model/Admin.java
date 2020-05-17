package com.chachae.exam.common.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.chachae.exam.common.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Date;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 管理员实体类
 *
 * @author chachae
 * @date 2020/1/15
 */
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Admin extends BaseEntity {

  @TableId
  private Integer id;

  /**
   * 姓名
   */
  @NotBlank(message = "姓名{required}")
  @Size(max = 20, message = "{noMoreThan}")
  private String name;

  /**
   * 用户名
   */
  @NotBlank(message = "用户名{required}")
  @Size(max = 20, message = "用户名{noMoreThan}")
  private String number;

  /**
   * 密码
   */
  @JsonIgnore
  @NotBlank(message = "密码{required}")
  @Size(max = 20, message = "{noMoreThan}")
  private String password;

  /**
   * 角色id
   */
  private Integer roleId;

  /**
   * 最后登录时间
   */
  private Date lastLoginTime;

  /**
   * 学院id
   */
  @NotNull(message = "管理员类型{required}")
  private Integer academyId;

}
