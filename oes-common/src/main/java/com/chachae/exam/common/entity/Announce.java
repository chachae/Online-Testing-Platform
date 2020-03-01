package com.chachae.exam.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 公告实体
 *
 * @author yzn
 */
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Announce extends Model<Announce> {
  @TableId(type = IdType.AUTO)
  private Integer id;

  /** 公告标题 */
  private String title;

  /** 公告内容 */
  private String content;

  /** 作者id */
  private String authorId;

  /** 作者身份id：教师_3,管理员_1 */
  private Integer roleId;

  /** 作者名 */
  private String authorName;

  /** 公告创建时间 */
  private Date createTime;
}
