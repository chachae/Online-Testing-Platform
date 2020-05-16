package com.chachae.exam.common.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.chachae.exam.common.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Date;
import javax.validation.constraints.NotNull;
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
  @NotNull(message = "姓名不能为空")
  private String name;
  /**
   * 工号
   */
  @NotNull(message = "工号不能为空")
  private String number;
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
   * 最后登录时间
   */
  private Date lastLoginTime;

  /**
   * 学院id
   */
  @NotNull(message = "请选择管理员类型")
  private Integer academyId;

}
