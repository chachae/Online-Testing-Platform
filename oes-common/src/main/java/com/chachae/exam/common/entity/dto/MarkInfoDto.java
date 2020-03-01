package com.chachae.exam.common.entity.dto;

import com.chachae.exam.common.base.BaseEntity;
import com.chachae.exam.common.entity.StuAnswerRecord;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 评分信息实体类
 *
 * @author yzn
 * @since 2020/2/6 21:46
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class MarkInfoDto extends BaseEntity {

  private Integer score;

  private List<String> wrongIds;

  private List<StuAnswerRecord> stuAnswerRecord;
}
