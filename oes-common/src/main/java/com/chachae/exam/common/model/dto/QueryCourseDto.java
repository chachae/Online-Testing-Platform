package com.chachae.exam.common.model.dto;

import com.chachae.exam.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author chachae
 * @since 2020/5/17 14:42
 */

@EqualsAndHashCode(callSuper = false)
@Data
public class QueryCourseDto extends BaseEntity {

  private Integer teacherId;

  private Integer academyId;

  private String key;

}
