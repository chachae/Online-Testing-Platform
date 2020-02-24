package com.exam.entity.dto;

import com.exam.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 导入试卷随机抽题数据传输对象
 *
 * @author chachae
 * @since 2020/2/23 22:30
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ImportPaperRandomQuestionDto extends BaseEntity {
  // 单项选择题
  private Integer a;
  private String aNum;
  private String aScore;
  private String aDif;

  // 多项选择题
  private Integer b;
  private String bNum;
  private String bScore;
  private String bDif;

  // 判断题
  private Integer c;
  private String cNum;
  private String cScore;
  private String cDif;

  // 填空题
  private Integer d;
  private String dNum;
  private String dScore;
  private String dDif;

  // 主观题
  private Integer e;
  private String eNum;
  private String eScore;
  private String eDif;

  // 编程题
  private Integer f;
  private String fNum;
  private String fScore;
  private String fDif;
}
