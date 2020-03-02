package com.chachae.exam.controller.common;

import com.chachae.exam.common.model.Question;
import com.chachae.exam.service.QuestionService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

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
   * @param isRandom 是否进行题目随机排序
   */
  public void setQuestionModel(ModelAndView mv, Integer paperId, boolean isRandom) {
    List<Question> qChoiceList = questionService.selectByPaperIdAndType(paperId, 1);
    List<Question> qMulChoiceList = questionService.selectByPaperIdAndType(paperId, 2);
    List<Question> qTofList = questionService.selectByPaperIdAndType(paperId, 3);
    List<Question> qFillList = questionService.selectByPaperIdAndType(paperId, 4);
    List<Question> qSaqList = questionService.selectByPaperIdAndType(paperId, 5);
    List<Question> qProgramList = questionService.selectByPaperIdAndType(paperId, 6);
    // 进行随机
    if (isRandom) {
      Collections.shuffle(qChoiceList);
      Collections.shuffle(qMulChoiceList);
      Collections.shuffle(qTofList);
      Collections.shuffle(qFillList);
      Collections.shuffle(qSaqList);
      Collections.shuffle(qProgramList);
    }
    mv.addObject("qChoiceList", qChoiceList);
    mv.addObject("qMulChoiceList", qMulChoiceList);
    mv.addObject("qTofList", qTofList);
    mv.addObject("qFillList", qFillList);
    mv.addObject("qSaqList", qSaqList);
    mv.addObject("qProgramList", qProgramList);
  }
}
