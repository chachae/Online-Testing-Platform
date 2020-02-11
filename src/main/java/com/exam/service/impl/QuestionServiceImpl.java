package com.exam.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.exam.constant.SysConsts;
import com.exam.entity.*;
import com.exam.entity.dto.ImportPaperDto;
import com.exam.entity.dto.QuestionDto;
import com.exam.entity.dto.StudentAnswerDto;
import com.exam.exception.ServiceException;
import com.exam.mapper.*;
import com.exam.service.QuestionService;
import com.exam.util.BeanUtil;
import com.exam.util.FileUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;
import java.util.Map;

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
  @Resource private PaperMapper paperMapper;
  @Resource private PaperFormMapper paperFormMapper;

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

  @Override
  @Transactional(rollbackFor = Exception.class)
  public ImportPaperDto importQuestion(MultipartFile multipartFile) {
    try {
      // 准备一个 Map 用来存储题目的类型和数量
      Map<Integer, Integer> typeNumMap = Maps.newHashMap();
      initMap(typeNumMap);

      // 准备一个 Map 用来存储题目的类型和分值
      Map<Integer, Integer> typeScoreMap = Maps.newHashMap();
      initMap(typeScoreMap);
      // 考试名称
      String paperName = FileUtil.getFileNameNoEx(multipartFile.getOriginalFilename());
      File file = FileUtil.toFile(multipartFile);
      ExcelReader reader = ExcelUtil.getReader(file);
      // 读取问题的信息
      List<QuestionDto> questions = reader.readAll(QuestionDto.class);
      // 插入问题表并组装ID
      List<Integer> idList = Lists.newArrayList();
      for (QuestionDto question : questions) {
        Question res = BeanUtil.copyObject(question, Question.class);
        System.out.println(res);
        this.questionMapper.insert(res);
        idList.add(res.getId());
        Integer typeId = question.getTypeId();
        Integer num = typeNumMap.get(typeId);
        num++;
        // 存储数量
        typeNumMap.put(typeId, num);
        // 存储分值
        typeScoreMap.put(typeId, question.getScore());
      }

      // 插入试卷模板信息
      PaperForm form = new PaperForm();
      // 设置数量分布
      form.setQChoiceNum(String.valueOf(typeNumMap.get(SysConsts.QUESTION.CHOICE_TYPE)));
      form.setQMulChoiceNum(String.valueOf(typeNumMap.get(SysConsts.QUESTION.MUL_CHOICE_TYPE)));
      form.setQTofNum(String.valueOf(typeNumMap.get(SysConsts.QUESTION.TOF_TYPE)));
      form.setQFillNum(String.valueOf(typeNumMap.get(SysConsts.QUESTION.FILL_TYPE)));
      form.setQSaqNum(String.valueOf(typeNumMap.get(SysConsts.QUESTION.SAQ_TYPE)));
      form.setQProgramNum(String.valueOf(typeNumMap.get(SysConsts.QUESTION.PROGRAM_TYPE)));
      // 设置单题分值
      form.setQChoiceScore(String.valueOf(typeScoreMap.get(SysConsts.QUESTION.CHOICE_TYPE)));
      form.setQMulChoiceScore(String.valueOf(typeScoreMap.get(SysConsts.QUESTION.MUL_CHOICE_TYPE)));
      form.setQTofScore(String.valueOf(typeScoreMap.get(SysConsts.QUESTION.TOF_TYPE)));
      form.setQFillScore(String.valueOf(typeScoreMap.get(SysConsts.QUESTION.FILL_TYPE)));
      form.setQSaqScore(String.valueOf(typeScoreMap.get(SysConsts.QUESTION.SAQ_TYPE)));
      form.setQProgramScore(String.valueOf(typeScoreMap.get(SysConsts.QUESTION.PROGRAM_TYPE)));
      this.paperFormMapper.insert(form);
      // 模板ID
      Integer formId = form.getId();
      ImportPaperDto dto = new ImportPaperDto();
      StringBuilder sb = new StringBuilder();
      for (Integer id : idList) {
        String idStr = String.valueOf(id);
        sb.append(idStr);
        sb.append(StrUtil.COMMA);
      }
      String ids = sb.toString();
      // 去除最后一个逗号并封装题序参数
      dto.setQuestionIdList(ids.substring(0, ids.length() - 1));
      // 封装问题信息参数
      dto.setPaperName(paperName);
      dto.setPaperFormId(formId);
      return dto;
    } catch (Exception e) {
      throw new ServiceException("试题解析失败");
    }
  }

  /**
   * 初始化Map
   *
   * @param map Map 对象
   */
  private void initMap(Map<Integer, Integer> map) {
    map.put(1, 0);
    map.put(2, 0);
    map.put(3, 0);
    map.put(4, 0);
    map.put(5, 0);
    map.put(6, 0);
  }
}
