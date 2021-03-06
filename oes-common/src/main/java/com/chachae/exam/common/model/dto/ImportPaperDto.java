package com.chachae.exam.common.model.dto;

import com.chachae.exam.common.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 导入试卷bean
 *
 * @author chachae
 * @since 2020/2/11 21:32
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportPaperDto extends BaseEntity {

  private Integer paperFormId;

  private Integer score;

  private String paperName;

  private String questionIdList;
}
