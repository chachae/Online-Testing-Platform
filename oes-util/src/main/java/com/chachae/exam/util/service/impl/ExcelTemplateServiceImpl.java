package com.chachae.exam.util.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.enums.WriteDirectionEnum;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.fill.FillConfig;
import com.alibaba.excel.write.metadata.fill.FillWrapper;
import com.chachae.exam.common.model.Paper;
import com.chachae.exam.common.model.Question;
import com.chachae.exam.common.model.Score;
import com.chachae.exam.util.service.ExcelTemplateService;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

/**
 * @author chachae
 * @since 2020/5/19 22:30
 */

@Slf4j
@Service
public class ExcelTemplateServiceImpl implements ExcelTemplateService {

  @Override
  public void packingPaperAnalysis(Map<String, Object> sheetMap, HttpServletResponse response) {
    try {
      // 获取模板Excel
      File file = ResourceUtils.getFile("classpath:excel/analysis.xlsx");
      // 文件名
      String fileName = URLEncoder
          .encode(sheetMap.get("paperName") + "成绩分析表", StandardCharsets.UTF_8.displayName());
      //响应头部信息
      this.setResponseHeader(response, fileName);
      //使用response.getOutputStream()下载,并使用项目下的模板填充
      ExcelWriter writer = EasyExcel.write(response.getOutputStream()).withTemplate(file).build();
      WriteSheet writeSheet = EasyExcel.writerSheet().build();

      writer.fill(sheetMap, writeSheet);
      writer.finish();
    } catch (IOException e) {
      log.warn(e.getMessage());
    }
  }

  @Override
  public void packingPaper(Paper paper, List<Question> questionList, HttpServletResponse response) {
    try {
      // 获取模板Excel
      File file = ResourceUtils.getFile("classpath:excel/export_paper.xlsx");
      // 文件名
      String fileName = URLEncoder
          .encode(paper.getPaperName() + "试卷导出", StandardCharsets.UTF_8.displayName());
      //响应头部信息
      this.setResponseHeader(response, fileName);
      //使用response.getOutputStream()下载,并使用项目下的模板填充
      ExcelWriter writer = EasyExcel.write(response.getOutputStream()).withTemplate(file).build();
      WriteSheet writeSheet = EasyExcel.writerSheet().build();
      writer.fill(paper, writeSheet);
      writer.fill(questionList, writeSheet);
      writer.finish();
    } catch (IOException e) {
      log.warn(e.getMessage());
    }
  }

  @Override
  public void packingStudentScoreAnalysis(List<Score> scoreList, HttpServletResponse response) {
    try {
      // 获取模板Excel
      File file = ResourceUtils.getFile("classpath:excel/student_analysis.xlsx");
      // 文件名
      String fileName = URLEncoder.encode("学生成绩分析表", StandardCharsets.UTF_8.displayName());
      //响应头部信息
      this.setResponseHeader(response, fileName);
      ExcelWriter writer = EasyExcel.write(response.getOutputStream()).withTemplate(file).build();
      WriteSheet writeSheet = EasyExcel.writerSheet().build();
      // 获取成绩
      List<Paper> paperList = scoreList.stream().map(Score::getPaper)
          .collect(Collectors.toList());
      writer.fill(new FillWrapper("scoreList", scoreList), writeSheet);
      writer.fill(new FillWrapper("paperList", paperList), writeSheet);
      writer.finish();
    } catch (IOException e) {
      log.warn(e.getMessage());
    }
  }

  /**
   * 响应头信息
   *
   * @param response 响应对象
   * @param fileName 文件名称
   */
  private void setResponseHeader(HttpServletResponse response, String fileName) {
    //响应头部信息
    String contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8";
    response.setContentType(contentType);
    response.setHeader("Content-Disposition",
        String.format("attachment;filename=%s.xlsx", fileName));
    response.setCharacterEncoding(StandardCharsets.UTF_8.displayName());
  }
}
