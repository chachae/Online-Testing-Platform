package com.chachae.exam.controller.common;

import com.chachae.exam.service.QuestionService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;

/**
 * @author chachae
 * @since 2020/2/25 21:16
 */
@Component
public class QuestionModel {

  @Resource private QuestionService questionService;

  /**
   * 设置题目的 model 对象信息
   *
   * @param mv model 对象
   * @param paperId 试卷id
   */
  public void setQuestionModel(ModelAndView mv, Integer paperId) {
    mv.addObject("qChoiceList", questionService.selectByPaperIdAndType(paperId, 1));
    mv.addObject("qMulChoiceList", questionService.selectByPaperIdAndType(paperId, 2));
    mv.addObject("qTofList", questionService.selectByPaperIdAndType(paperId, 3));
    mv.addObject("qFillList", questionService.selectByPaperIdAndType(paperId, 4));
    mv.addObject("qSaqList", questionService.selectByPaperIdAndType(paperId, 5));
    mv.addObject("qProgramList", questionService.selectByPaperIdAndType(paperId, 6));
  }
}
