package com.chachae.exam.common.model.dto;

import com.chachae.exam.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author chachae
 * @since 2020/3/11 20:47
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class QueryQuestionDto extends BaseEntity {

  private Integer courseId;

  private Integer typeId;

  private String questionName;
}
