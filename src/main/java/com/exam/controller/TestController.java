package com.exam.controller;

import com.exam.service.QuestionService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author yzn
 * @since 2020/2/11 20:21
 */
@RestController
@RequestMapping("/test")
public class TestController {

  @Resource private QuestionService questionService;
}
