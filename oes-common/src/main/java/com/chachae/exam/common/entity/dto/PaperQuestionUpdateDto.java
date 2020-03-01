package com.chachae.exam.common.entity.dto;

import com.chachae.exam.common.base.BaseEntity;
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
