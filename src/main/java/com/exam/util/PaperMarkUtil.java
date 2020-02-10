package com.exam.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.exam.entity.Question;
import com.exam.entity.StuAnswerRecord;
import com.exam.entity.dto.MarkInfoDto;
import com.google.common.collect.Lists;
import org.apache.commons.text.similarity.JaccardSimilarity;

import javax.servlet.http.HttpServletRequest;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Set;

/**
 * 评分工具
 *
 * @author yzn
 * @since 2020/2/6 21:39
 */
public class PaperMarkUtil {

  /**
   * 基本评分方法
   *
   * @param set 问题集合
   * @param score 题目分值
   * @param request request 对象
   * @return 题型所得分值
   */
  public static MarkInfoDto mark(Set<Question> set, int score, HttpServletRequest request) {
    int sum = 0;
    List<String> wrongIds = Lists.newArrayList();
    if (CollUtil.isNotEmpty(set)) {
      for (Question q : set) {
        // 获取问题题目序号
        String res = request.getParameter(String.valueOf(q.getId()));
        // 如果选项正确，加分
        if (q.getAnswer().equals(res)) {
          sum += score;
        } else {
          // 选择错误，加入错题集
          wrongIds.add(String.valueOf(q.getId()));
        }
      }
    }
    // 组装信息
    MarkInfoDto info = new MarkInfoDto();
    info.setScore(sum);
    info.setWrongIds(wrongIds);
    return info;
  }

  /**
   * 多选题批改
   *
   * @param set 问题集合
   * @param score 题目分值
   * @param request request 对象
   * @return 题型所得分值
   */
  public static MarkInfoDto mulMark(Set<Question> set, int score, HttpServletRequest request) {
    int sum = 0;
    List<String> wrongIds = Lists.newArrayList();
    if (CollUtil.isNotEmpty(set)) {
      for (Question q : set) {
        String[] res = request.getParameterValues(String.valueOf(q.getId()));
        // 没有选答案就给零分
        if (res != null) {
          String result = "";
          for (String s : res) {
            result = s + StrUtil.COMMA;
          }
          result = result.substring(0, result.length() - 1);
          if (result.equals(q.getAnswer())) {
            sum += score;
          } else {
            wrongIds.add(String.valueOf(q.getId()));
          }
        } else {
          wrongIds.add(String.valueOf(q.getId()));
        }
      }
    }
    // 组装信息
    MarkInfoDto info = new MarkInfoDto();
    info.setScore(sum);
    info.setWrongIds(wrongIds);
    return info;
  }

  /**
   * 主观题评分
   *
   * @param set 问题集合
   * @param score 题目分值
   * @param request request 对象
   * @return 题型所得分值
   */
  public static MarkInfoDto essayMark(Set<Question> set, double score, HttpServletRequest request) {
    int sum = 0;
    // 错题集
    List<String> wrongIds = Lists.newArrayList();
    // 主观题/编程题答题记录
    List<StuAnswerRecord> stuAnswerRecords = Lists.newArrayList();
    JaccardSimilarity jaccardSimilarity = new JaccardSimilarity();
    // 是NumberFormat的一个具体子类，用于格式化十进制数字，此处用于将数字转化为String
    // #表示整数
    DecimalFormat df = new DecimalFormat("#");
    // 简答题批改
    if (CollUtil.isNotEmpty(set)) {
      double f = 0;
      for (Question q : set) {
        wrongIds.add(String.valueOf(q.getId()));
        String res = request.getParameter(String.valueOf(q.getId()));
        String answer = q.getAnswer();
        // 计算 jaccard 相似系数
        double jcdSimilarity = jaccardSimilarity.apply(res, answer);
        double s = jcdSimilarity * score;
        f += s;
        // 封装主观题答题记录参数
        StuAnswerRecord stuAnswerRecord = new StuAnswerRecord();
        stuAnswerRecord.setQuestionId(q.getId());
        stuAnswerRecord.setAnswer(res);
        stuAnswerRecords.add(stuAnswerRecord);
      }
      String q = df.format(f);
      sum = Integer.parseInt(q);
    }
    // 封装参数
    MarkInfoDto info = new MarkInfoDto();
    // 得分
    info.setScore(sum);
    // 错题
    info.setWrongIds(wrongIds);
    // 答题记录
    List<StuAnswerRecord> stuAnswerRecord = Lists.newArrayList();
    stuAnswerRecord.addAll(stuAnswerRecords);
    info.setStuAnswerRecord(stuAnswerRecord);
    return info;
  }
}
