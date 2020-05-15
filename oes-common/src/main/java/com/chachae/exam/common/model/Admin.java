package com.chachae.exam.common.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.chachae.exam.common.base.BaseEntity;
import java.util.Date;
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
  // 姓名
  private String name;
  // 工号
  private String number;
  // 密码
  private String password;
  // 角色id
  private Integer roleId;
  // 最后登录时间
  private Date lastLoginTime;

}
