package com.chachae.exam.common.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author chachae
 * @since 2020/5/18 21:53
 */

@EqualsAndHashCode(callSuper = false)
@Data
public class QueryGradeDto {

  private Integer level;

  private String key;

  private Integer academyId;

  private Integer majorId;
}
