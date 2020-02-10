package com.exam.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.exam.entity.Course;
import com.exam.entity.Question;
import com.exam.entity.StuAnswerRecord;
import com.exam.entity.Type;
import com.exam.entity.dto.StudentAnswerDto;
import com.exam.exception.ServiceException;
import com.exam.mapper.CourseMapper;
import com.exam.mapper.QuestionMapper;
import com.exam.mapper.TypeMapper;
import com.exam.service.QuestionService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 试题业务实现
 *
 * @author yzn
 * @date 2020/2/1
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class QuestionServiceImpl implements QuestionService {

  @Resource private QuestionMapper questionMapper;
  @Resource private CourseMapper courseMapper;
  @Resource private TypeMapper typeMapper;

  @Override
  public PageInfo<Question> pageForQuestionList(Integer pageNo) {
    // 设置分页信息，默认每页显示8条数据，此处采用 PageHelper 物理分页插件实现数据分页
    PageHelper.startPage(pageNo, 10);
    // 查询试题集合信息
    List<Question> questionList = questionMapper.selectList(null);
    return new PageInfo<>(questionList);
  }

  @Override
  public Question findById(Integer id) {
    // 通过 ID 查询试题信息
    return questionMapper.selectById(id);
  }

  @Override
  public Course findByCourseId(Integer courseId) {
    // 通过 ID 查询课程查询接口
    return courseMapper.selectById(courseId);
  }

  @Override
  public List<Type> findAllType() {
    // 调用查询全部类型集合的接口
    return typeMapper.selectList(null);
  }

  @Override
  public List<Course> findTeacherCourse(Integer teacherId) {
    QueryWrapper<Course> qw = new QueryWrapper<>();
    qw.lambda().eq(Course::getTeacherId, teacherId);
    return this.courseMapper.selectList(qw);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void saveNewQuestion(Question question) {
    questionMapper.insert(question);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void editQuestion(Integer id, String questionName, String answer, String remark)
      throws ServiceException {
    Question question = questionMapper.selectById(id);
    if (question != null) {
      // 封装数据
      question.setQuestionName(questionName);
      question.setAnswer(answer);
      question.setRemark(remark);
      questionMapper.updateById(question);
    } else {
      throw new ServiceException("试题不存在");
    }
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deleteQuestion(Integer id) {
    questionMapper.deleteById(id);
  }

  @Override
  public List<Integer> getIdList(Integer typeId, Integer courseId) {
    // 使用 QueryWrapper 条件构造器构造 Sql 条件
    QueryWrapper<Question> qw = new QueryWrapper<>();
    qw.lambda().eq(Question::getTypeId, typeId).eq(Question::getCourseId, courseId);
    // 获取所有对饮条件的问题集合
    List<Question> questions = questionMapper.selectList(qw);
    // 准备用于存储 ID 的集合
    List<Integer> idList = Lists.newArrayList();
    // 循环问题集合获取问题 ID，将 ID 加入idList 中
    questions.forEach(question -> idList.add(question.getId()));
    return idList;
  }

  @Override
  public List<Question> findByAnswerRecordList(List<StuAnswerRecord> answerRecordList)
      throws ServiceException {
    if (CollUtil.isEmpty(answerRecordList)) {
      throw new ServiceException("未找到主观题答案记录！");
    }
    // 获取当前所有ID主观题的正确答案并返回结果集合
    List<Question> questionList = Lists.newArrayList();
    for (StuAnswerRecord answerRecord : answerRecordList) {
      // 调用问题查询接口
      Question question = questionMapper.selectById(answerRecord.getQuestionId());
      questionList.add(question);
    }
    return questionList;
  }

  @Override
  public List<StudentAnswerDto> findMapByStuAnswerRecordAndQuestionList(
      List<StuAnswerRecord> answerRecordList, List<Question> questionList) {
    List<StudentAnswerDto> res = Lists.newArrayList();
    // 循环问题集合
    for (Question question : questionList) {
      // 循环学生的答题记录集合
      for (StuAnswerRecord s : answerRecordList) {
        // 比较问题的 ID 是否和学生答题记录所属 ID 相同
        if (question.getId().equals(s.getQuestionId())) {
          // 将学生答题记录的问题题目和回答内容组装发到
          StudentAnswerDto dto = new StudentAnswerDto();
          dto.setId(s.getId());
          dto.setQuestionName(question.getQuestionName());
          dto.setAnswer(s.getAnswer());
          dto.setScore(s.getScore());
          res.add(dto);
        }
      }
    }
    return res;
  }
}
