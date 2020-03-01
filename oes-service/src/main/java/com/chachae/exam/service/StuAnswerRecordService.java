package com.chachae.exam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chachae.exam.common.entity.StuAnswerRecord;
import com.chachae.exam.common.entity.dto.StuAnswerRecordDto;

import java.util.List;

/**
 * 学生主观题答题记录表服务接口
 *
 * @author yzn
 * @since 2020-02-07 21:49:52
 */
public interface StuAnswerRecordService extends IService<StuAnswerRecord> {

  /**
   * 根据试卷ID查找复查试题记录
   *
   * @param paperId 试卷ID
   * @return 答题信息
   */
  List<StuAnswerRecord> selectByPaperId(Integer paperId);

  /**
   * 通过学生 ID 删除答题记录
   *
   * @param stuId 学生ID
   */
  void deleteByStuId(Integer stuId);

  /**
   * 通过试卷ID查询学生答题记录集合传输对象
   *
   * @param paperId 试卷ID
   * @return 集合对象
   */
  List<StuAnswerRecordDto> listStuAnswerRecordDto(Integer paperId);
}
