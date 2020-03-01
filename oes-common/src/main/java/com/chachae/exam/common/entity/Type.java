package com.chachae.exam.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 题目类型实体类
 *
 * @author yzn
 * @date 2020/2/8
 */
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Type extends Model<Type> {
  @TableId(type = IdType.AUTO)
  private Integer id;

  /** 题目类型 */
  private String typeName;

  /** 各个类型题目的分数 */
  private String score;

  /** 该类型题目说明 */
  private String remark;
}
