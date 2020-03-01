package com.chachae.exam.common.entity.dto;

import com.chachae.exam.common.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 学生查询 bean
 *
 * @author yzn
 * @since 2020/2/9 21:29
 */
@EqualsAndHashCode(callSuper = false)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentQueryDto extends BaseEntity {

  private String name;

  private Integer academyId;

  private Integer majorId;
}
