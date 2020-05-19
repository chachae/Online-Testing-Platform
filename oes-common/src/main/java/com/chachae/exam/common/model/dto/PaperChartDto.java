package com.chachae.exam.common.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author chachae
 * @since 2020/5/19 16:30
 */

@EqualsAndHashCode(callSuper = false)
@Data
public class PaperChartDto {

  private Integer paperId;

  private Integer level;

  private Integer gradeId;

  private Integer majorId;

}
