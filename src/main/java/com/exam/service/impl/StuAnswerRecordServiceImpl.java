package com.exam.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exam.entity.StuAnswerRecord;
import com.exam.entity.Student;
import com.exam.exception.ServiceException;
import com.exam.mapper.StuAnswerRecordMapper;
import com.exam.mapper.StudentMapper;
import com.exam.service.StuAnswerRecordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

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
  @Resource private StuAnswerRecordMapper stuAnswerRecordMapper;

  @Override
  public List<StuAnswerRecord> selectByStuAndPaper(String stuNumber, Integer paperId) {
    // 通过学号查询学生是否存在
    QueryWrapper<Student> qw = new QueryWrapper<>();
    qw.lambda().eq(Student::getStuNumber, stuNumber);
    Student student = studentMapper.selectOne(qw);
    if (ObjectUtil.isEmpty(student)) {
      throw new ServiceException("该学号不存在！");
    }
    // 构造根据学号和试卷ID条件查询语句
    QueryWrapper<StuAnswerRecord> ansQw = new QueryWrapper<>();
    // 构造学生 ID 和试卷 ID 查询条件
    ansQw.lambda().eq(StuAnswerRecord::getStuId, student.getId());
    ansQw.lambda().eq(StuAnswerRecord::getPaperId, paperId);
    return stuAnswerRecordMapper.selectList(ansQw);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deleteByStuId(Integer stuId) {
    QueryWrapper<StuAnswerRecord> qw = new QueryWrapper<>();
    qw.lambda().eq(StuAnswerRecord::getStuId, stuId);
    this.stuAnswerRecordMapper.delete(qw);
  }
}
