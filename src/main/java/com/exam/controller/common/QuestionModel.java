package com.exam.controller.common;

import com.exam.service.QuestionService;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import javax.annotation.Resource;

/**
 * @author yzn
 * @since 2020/2/25 21:16
 */
@Component
public class QuestionModel {

  @Resource private QuestionService questionService;

  /**
   * 设置题目的 model 对象信息
   *
   * @param model model 对象
   * @param paperId 试卷id
   */
  public void setQuestionModel(Model model, Integer paperId) {
    model.addAttribute("qChoiceList", questionService.selectByPaperIdAndType(paperId, 1));
    model.addAttribute("qMulChoiceList", questionService.selectByPaperIdAndType(paperId, 2));
    model.addAttribute("qTofList", questionService.selectByPaperIdAndType(paperId, 3));
    model.addAttribute("qFillList", questionService.selectByPaperIdAndType(paperId, 4));
    model.addAttribute("qSaqList", questionService.selectByPaperIdAndType(paperId, 5));
    model.addAttribute("qProgramList", questionService.selectByPaperIdAndType(paperId, 6));
  }
}
