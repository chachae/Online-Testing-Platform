package com.chachae.exam.util.service;

import com.chachae.exam.common.model.Paper;
import com.chachae.exam.common.model.Question;
import com.chachae.exam.common.model.Score;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

/**
 * @author chachae
 * @since 2020/5/19 22:14
 */
public interface ExcelTemplateService {

  /**
   * 填充班级考情分析表 Excel 导出接口
   *
   * @param sheetMap 表数据
   * @param response 响应对象
   */
  void packingPaperAnalysis(Map<String, Object> sheetMap, HttpServletResponse response);

  /**
   * 填充试卷
   *
   * @param questionList 问题集合
   * @param response     响应对象
   */
  void packingPaper(Paper paper, List<Question> questionList, HttpServletResponse response);

  /**
   * 填充试卷
   *
   * @param questionList 问题集合
   * @param response     响应对象
   */
  void packingStudentScoreAnalysis(List<Score> questionList, HttpServletResponse response);
}
