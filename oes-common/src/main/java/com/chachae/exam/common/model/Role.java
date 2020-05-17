package com.chachae.exam.common.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.chachae.exam.common.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 角色表实体类
 *
 * @author chachae
 * @since 2020-02-08 21:20:01
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Role extends BaseEntity {

  /**
   * 角色id:管理员_1，学生_2，老师_3
   */
  @TableId
  private Integer id;

  /**
   * 角色姓名
   */
  private String roleName;
}
