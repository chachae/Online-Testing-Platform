package com.chachae.exam.common.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * (RolePermission)表实体类
 *
 * @author chachae
 * @since 2020-03-01 16:02:05
 */
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RolePermission {

  @TableId(type = IdType.INPUT)
  private Integer roleId;

  private Integer permissionId;
}
