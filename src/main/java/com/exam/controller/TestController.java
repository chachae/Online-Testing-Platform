package com.exam.controller;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.exam.common.R;
import com.exam.entity.Question;
import com.exam.entity.dto.ImportPaperDto;
import com.exam.service.QuestionService;
import com.exam.util.FileUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;

/**
 * @author chachae
 * @since 2020/2/11 20:21
 */
@RestController
@RequestMapping("/test")
public class TestController {

  @Resource private QuestionService questionService;

  @PostMapping("/upload/excel")
  public R excel(@RequestParam("file") MultipartFile multipartFile) {
    try{
      ImportPaperDto dto = this.questionService.importQuestion(multipartFile);
      return R.successWithData(dto);
    }catch (Exception e){
      return R.error(e.getMessage());
    }
  }
}
