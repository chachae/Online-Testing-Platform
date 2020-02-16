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
import java.util.stream.Stream;

/**
 * 评分工具
 *
 * @author yzn
 * @since 2020/2/6 21:39
 */
public class PaperMarkUtil {

  private PaperMarkUtil() {}

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
    // 构建错题集集合
    List<String> wrongIds = Lists.newArrayList();
    // 判断问题集合不为空，空的话说明直接加入错题集
    if (CollUtil.isNotEmpty(set)) {
      // 循环正确答案
      for (Question q : set) {
        // 从 request 对象中获取多选题参数
        String[] res = request.getParameterValues(String.valueOf(q.getId()));
        // 没有选答案就给零分
        if (res != null) {
          StringBuilder sb = StrUtil.builder();
          // 拼接学生的多选题答案
          Stream.of(res).forEach(s -> sb.append(s).append(StrUtil.COMMA));
          // 转 String 类型
          String result = sb.toString();
          // 去除最后一位的逗号
          result = result.substring(0, result.length() - 1);
          // 计算得分
          if (result.equals(q.getAnswer())) {
            sum += score;
          } else {
            // 错误则加入错题集
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
        // 不管主观题答题的质量如何，都放入错题集中
        wrongIds.add(String.valueOf(q.getId()));
        // 从 request 对象中获取参数值
        String res = request.getParameter(String.valueOf(q.getId()));
        // 获取正确答案
        String answer = q.getAnswer();
        // 计算 jaccard 相似系数
        double jcdSimilarity = jaccardSimilarity.apply(res, answer);
        // 计算基于相似系数计算的基础分数（1. 相当于计算关键词的得分）
        double s = jcdSimilarity * score;
        f += s;
        // 字数不足且分数大于1，扣1分（2. 相当于计算字数）
        if (res.length() < answer.length() && f > 1) {
          f -= 1;
        }
        // 封装主观题答题记录参数
        StuAnswerRecord stuAnswerRecord = new StuAnswerRecord();
        stuAnswerRecord.setQuestionId(q.getId());
        stuAnswerRecord.setAnswer(res);
        stuAnswerRecords.add(stuAnswerRecord);
      }
      // 格式化分数的类型为 int 类型
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
