package com.exam.entity.dto;

import com.exam.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author yzn
 * @since 2020/2/21 16:05
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class PaperQuestionUpdateDto extends BaseEntity {

  private Integer paperId;

  private Integer oldId;

  private Integer newId;
}
