package com.chachae.exam.common.model.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 增加班级实体类
 *
 * @author chachae
 * @since 2020/5/18 22:33
 */

@EqualsAndHashCode(callSuper = false)
@Data
public class ImportGradeDto {

  @NotNull(message = "年级{required}")
  private Integer level;

  @NotNull(message = "专业{required}")
  private Integer majorId;

  @NotBlank(message = "班级{required}")
  private String gradeNumbers;


}
