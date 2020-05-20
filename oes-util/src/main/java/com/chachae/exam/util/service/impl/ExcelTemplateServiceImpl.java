package com.chachae.exam.util.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.chachae.exam.util.service.ExcelTemplateService;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
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

  private final String contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8";

  @Override
  public void packingPaperAnalysis(Map<String, Object> sheetMap, HttpServletResponse response) {
    try {
      // 获取模板Excel
      File file = ResourceUtils.getFile("classpath:excel/analysis.xlsx");

      // 文件名
      String fileName = URLEncoder.encode(sheetMap.get("paperName") + "成绩分析表.xlsx", StandardCharsets.UTF_8.displayName());

      //响应头部信息
      response.setContentType(contentType);
      response.setHeader("Content-Disposition",
          String.format("attachment;filename=%s.xlsx", fileName));
      response.setCharacterEncoding(StandardCharsets.UTF_8.displayName());

      //使用response.getOutputStream()下载,并使用项目下的模板填充
      ExcelWriter writer = EasyExcel.write(response.getOutputStream()).withTemplate(file).build();
      WriteSheet writeSheet = EasyExcel.writerSheet().build();

      writer.fill(sheetMap, writeSheet);
      writer.finish();
    } catch (IOException e) {
      log.warn("模板不存在");
    }
  }
}
