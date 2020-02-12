package com.exam.entity.dto;

import com.exam.common.BaseEntity;
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
@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportPaperDto extends BaseEntity {

  private Integer paperFormId;

  private String paperName;

  private String questionIdList;
}
