package com.chachae.exam.common.model.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

/**
 * @author chachae
 * @since 2020/5/19 16:30
 */

@EqualsAndHashCode(callSuper = false)
@Data
public class PaperChartDto {

  @NotNull(message = "场次{required}")
  private Integer paperId;

  @NotNull(message = "年级{required}")
  private Integer level;

  @NotNull(message = "班级{required}")
  private Integer gradeId;

  @NotNull(message = "专业{required}")
  private Integer majorId;

}
