package com.exam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.exam.entity.StuAnswerRecord;

import java.util.List;

/**
 * 学生主观题答题记录表服务接口
 *
 * @author yzn
 * @since 2020-02-07 21:49:52
 */
public interface StuAnswerRecordService extends IService<StuAnswerRecord> {

  /**
   * 根据学生和试卷查找复查试题记录
   *
   * @param stuNumber 学号
   * @param paperId 试卷ID
   * @return 答题信息
   */
  List<StuAnswerRecord> selectByStuAndPaper(String stuNumber, Integer paperId);
}
