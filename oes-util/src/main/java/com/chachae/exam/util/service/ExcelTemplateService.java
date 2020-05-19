package com.chachae.exam.util.service;

import com.chachae.exam.util.model.PaperAnalysis;
import javax.servlet.http.HttpServletResponse;

/**
 * @author chachae
 * @since 2020/5/19 22:14
 */
public interface ExcelTemplateService {

  /**
   * 填充班级考情分析表 Excel
   *
   * @param entity 表数据
   */
  void packingPaperAnalysis(PaperAnalysis entity, HttpServletResponse response);

}
