package com.chachae.exam.common.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 专业信息实体
 *
 * @author chachae
 * @date 2020/1/13
 */
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Major extends Model<Major> {
  @TableId(type = IdType.AUTO)
  private Integer id;

  /** 专业班级 */
  private String major;

  /** 学院 */
  private Integer academyId;
}
