package com.exam.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exam.entity.Question;
import com.exam.entity.Score;
import com.exam.entity.StuAnswerRecord;
import com.exam.entity.Student;
import com.exam.entity.dto.StuAnswerRecordDto;
import com.exam.entity.dto.StudentAnswerDto;
import com.exam.exception.ServiceException;
import com.exam.mapper.QuestionMapper;
import com.exam.mapper.StuAnswerRecordMapper;
import com.exam.mapper.StudentMapper;
import com.exam.service.ScoreService;
import com.exam.service.StuAnswerRecordService;
import com.exam.util.BeanUtil;
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
 * @author yzn
 * @since 2020-02-07 21:49:52
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class StuAnswerRecordServiceImpl extends ServiceImpl<StuAnswerRecordMapper, StuAnswerRecord>
    implements StuAnswerRecordService {

  @Resource private StudentMapper studentMapper;
  @Resource private ScoreService scoreService;
  @Resource private QuestionMapper questionMapper;
  @Resource private StuAnswerRecordMapper stuAnswerRecordMapper;

  @Override
  public List<StuAnswerRecord> selectByPaperId(Integer paperId) {
    QueryWrapper<StuAnswerRecord> qw = new QueryWrapper<>();
    qw.lambda().eq(StuAnswerRecord::getPaperId, paperId);
    List<StuAnswerRecord> result = this.stuAnswerRecordMapper.selectList(qw);
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
    this.stuAnswerRecordMapper.delete(qw);
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
      Student student = this.studentMapper.selectById(stuId);
      // 查询该考生该本考试的成绩信息
      Score score = this.scoreService.selectByStuIdAndPaperId(stuId, paperId);

      // 拷贝对象信息
      List<StudentAnswerDto> rec = BeanUtil.copyList(record, StudentAnswerDto.class);
      // 遍历查询并封装题目名称
      for (StudentAnswerDto studentAnswerDto : rec) {
        Question qs = this.questionMapper.selectById(studentAnswerDto.getQuestionId());
        studentAnswerDto.setQuestionName(qs.getQuestionName());
      }

      // 封装学生答题记录数据传输对象信息
      StuAnswerRecordDto dto = new StuAnswerRecordDto();
      // 封装传输对象参数
      dto.setStudent(student).setScore(score).setRecords(rec);
      // 加入学生答题记录数据传输集合中
      stuAnswerRecordDtoList.add(dto);
    }

    return stuAnswerRecordDtoList;
  }
}
