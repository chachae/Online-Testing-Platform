package com.chachae.exam.common.model.dto;

import com.chachae.exam.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 学生查询 bean
 *
 * @author chachae
 * @since 2020/2/9 21:29
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class QueryStudentDto extends BaseEntity {

  // 查询关键词
  private String key;

  private Integer academyId;
}
