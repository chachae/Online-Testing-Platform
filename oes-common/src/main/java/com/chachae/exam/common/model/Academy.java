package com.chachae.exam.common.model;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 学院表实体类
 *
 * @author chachae
 * @since 2020-02-09 12:09:59
 */
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Data
public class Academy {

  // 学院id
  @TableId
  private Integer id;
  // 学院名称
  private String name;

}
