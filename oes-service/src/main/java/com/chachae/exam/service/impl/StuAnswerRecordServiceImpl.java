package com.chachae.exam.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chachae.exam.common.dao.QuestionDAO;
import com.chachae.exam.common.dao.StuAnswerRecordDAO;
import com.chachae.exam.common.dao.StudentDAO;
import com.chachae.exam.common.exception.ServiceException;
import com.chachae.exam.common.model.Question;
import com.chachae.exam.common.model.Score;
import com.chachae.exam.common.model.StuAnswerRecord;
import com.chachae.exam.common.model.Student;
import com.chachae.exam.common.model.dto.StuAnswerRecordDto;
import com.chachae.exam.common.model.dto.StudentAnswerDto;
import com.chachae.exam.common.util.BeanUtil;
import com.chachae.exam.service.ScoreService;
import com.chachae.exam.service.StuAnswerRecordService;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 学生主观题答题记录业务实现类
 *
 * @author chachae
 * @since 2020-02-07 21:49:52
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class StuAnswerRecordServiceImpl extends ServiceImpl<StuAnswerRecordDAO, StuAnswerRecord>
    implements StuAnswerRecordService {

  @Resource private StudentDAO studentDAO;
  @Resource private ScoreService scoreService;
  @Resource private QuestionDAO questionDAO;
  @Resource private StuAnswerRecordDAO stuAnswerRecordDAO;

  @Override
  public List<StuAnswerRecord> selectByPaperId(Integer paperId) {
    // 构造通过试卷 ID 查詢答题记录的条件
    QueryWrapper<StuAnswerRecord> qw = new QueryWrapper<>();
    qw.lambda().eq(StuAnswerRecord::getPaperId, paperId);
    List<StuAnswerRecord> result = this.stuAnswerRecordDAO.selectList(qw);
    // 检查集合对象的情况
    if (CollUtil.isEmpty(result)) {
      throw new ServiceException("这场试卷不存待复查的主观题");
    } else {
      return result;
    }
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deleteByStuId(Integer stuId) {
    QueryWrapper<StuAnswerRecord> qw = new QueryWrapper<>();
    qw.lambda().eq(StuAnswerRecord::getStuId, stuId);
    this.stuAnswerRecordDAO.delete(qw);
  }

  @Override
  public List<StuAnswerRecordDto> listStuAnswerRecordDto(Integer paperId) {
    // 学生答题记录数据传输集合
    List<StuAnswerRecordDto> stuAnswerRecordDtoList = Lists.newArrayList();
    List<StuAnswerRecord> stuAnswerRecords = this.selectByPaperId(paperId);

    // 存在主观题，进行排序和过滤
    Collection<List<StuAnswerRecord>> values =
        stuAnswerRecords.stream()
            // 根据问题的 ID 排序
            .sorted(Comparator.comparingInt(StuAnswerRecord::getQuestionId))
            // 根据学生 ID 分组
            .collect(Collectors.groupingBy(StuAnswerRecord::getStuId))
            // 取出集合
            .values();

    // 循环设置参数
    for (List<StuAnswerRecord> record : values) {
      // 取出索引位置 0 的学生 ID
      int stuId = record.get(0).getStuId();
      // 查询到该学生学生信息
      Student student = this.studentDAO.selectById(stuId);
      // 查询该考生该本考试的成绩信息
      Score score = this.scoreService.selectByStuIdAndPaperId(stuId, paperId);

      // 拷贝对象信息
      List<StudentAnswerDto> rec = BeanUtil.copyList(record, StudentAnswerDto.class);
      // 遍历查询并封装题目名称
      for (StudentAnswerDto studentAnswerDto : rec) {
        Question qs = this.questionDAO.selectById(studentAnswerDto.getQuestionId());
        studentAnswerDto.setQuestionName(qs.getQuestionName());
      }

      // 封装学生答题记录数据传输对象信息
      StuAnswerRecordDto dto = new StuAnswerRecordDto();
      // 封装传输对象参数
      dto.setStudent(student).setScore(score).setRecords(rec);
      // 加入学生答题记录数据传输集合中
      stuAnswerRecordDtoList.add(dto);
    }

    // 返回集合数据
    return stuAnswerRecordDtoList;
  }
}
