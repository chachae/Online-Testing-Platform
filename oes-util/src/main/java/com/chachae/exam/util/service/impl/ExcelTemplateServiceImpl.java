package com.chachae.exam.util.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.chachae.exam.util.model.PaperAnalysis;
import com.chachae.exam.util.service.ExcelTemplateService;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
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
  public void packingPaperAnalysis(PaperAnalysis entity, HttpServletResponse response) {
    try {
      // 获取模板Excel
      File file = ResourceUtils.getFile("classpath:excel/analysis.xlsx");
      // 文件名称
      String fileName = entity.getPaperName() + "成绩分析表.xlsx";
      response.setContentType("application/vnd.ms-excel");
      response.setCharacterEncoding(StandardCharsets.UTF_8.displayName());
      String filename = URLEncoder.encode(fileName, StandardCharsets.UTF_8.displayName());
      response.setHeader("Content-disposition", "attachment;filename=" + filename + ".xls");
      //使用response.getOutputStream()下载,并使用项目下的模板填充
      ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream()).withTemplate(file)
          .build();
      WriteSheet writeSheet = EasyExcel.writerSheet().build();
      HashMap<String, Object> map = new HashMap<>();
      map.put("paperName", entity.getPaperName());
      map.put("academyName", entity.getAcademyName());
      map.put("courseAcademyName", entity.getCourseAcademyName());
      map.put("courseName", entity.getCourseName());
      map.put("gradeName", entity.getGradeName());
      map.put("level", entity.getLevel());
      map.put("major", entity.getMajor());
      map.put("studentCount", entity.getStudentCount());
      map.put("teacherName", entity.getTeacherName());
      excelWriter.fill(map, writeSheet);
      excelWriter.finish();
    } catch (IOException e) {
      log.warn("模板不存在");
    }
  }
}
