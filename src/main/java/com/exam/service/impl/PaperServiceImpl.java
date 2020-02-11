package com.exam.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.Log;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exam.constant.SysConsts;
import com.exam.entity.*;
import com.exam.entity.dto.MarkInfoDto;
import com.exam.exception.ServiceException;
import com.exam.mapper.*;
import com.exam.service.PaperService;
import com.exam.service.QuestionService;
import com.exam.util.DateUtil;
import com.exam.util.NumberUtil;
import com.exam.util.PaperMarkUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * 试卷批改业务实现
 *
 * @author yzn
 * @date 2020/2/2
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class PaperServiceImpl extends ServiceImpl<PaperMapper, Paper> implements PaperService {

  private Log logger = Log.get();

  @Resource private PaperMapper paperMapper;
  @Resource private CourseMapper courseMapper;
  @Resource private PaperFormMapper paperFormMapper;
  @Resource private MajorMapper majorMapper;
  @Resource private QuestionService questionService;
  @Resource private QuestionMapper questionMapper;
  @Resource private StuAnswerRecordMapper stuAnswerRecordMapper;
  @Resource private ScoreMapper scoreMapper;
  @Resource private StudentMapper studentMapper;
  @Resource private TeacherMapper teacherMapper;

  @Override
  public PageInfo<Paper> pageForPaperList(Integer teacherId, Integer pageNo) {
    // 构造条件询条件
    QueryWrapper<Paper> qw = new QueryWrapper<>();
    qw.lambda().eq(Paper::getTeacherId, teacherId);
    // 分页查询，默认每页8条数据
    PageHelper.startPage(pageNo, 8);
    List<Paper> paperList = paperMapper.selectList(qw);
    return new PageInfo<>(paperList);
  }

  @Override
  public Course findCourseById(Integer courseId) {
    return courseMapper.selectById(courseId);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void newPaperForm(PaperForm paperForm) {
    paperFormMapper.insert(paperForm);
  }

  @Override
  public List<Course> findCourseListByTeacherId(Integer teacherId) {
    // 使用条件构造器构造查询条件
    QueryWrapper<Course> qw = new QueryWrapper<>();
    qw.lambda().eq(Course::getTeacherId, teacherId);
    return this.courseMapper.selectList(qw);
  }

  @Override
  public void newPaper(Paper paper) {
    // 获取试卷ID
    Integer paperFormId = paper.getPaperFormId();
    // 获取试卷题型信息
    PaperForm paperForm = paperFormMapper.selectById(paperFormId);
    // 获取试卷归属的课程 ID
    Integer courseId = paper.getCourseId();
    // 根据模板信息获取各类型题目的数量
    String qChoiceNum = paperForm.getQChoiceNum();
    String qMulChoiceNum = paperForm.getQMulChoiceNum();
    String qTofNum = paperForm.getQTofNum();
    String qFillNum = paperForm.getQFillNum();
    String qSaqNum = paperForm.getQSaqNum();
    String qProgramNum = paperForm.getQProgramNum();
    // 预先准备试卷问题集合
    List<Integer> paperQuestionIdList = Lists.newArrayList();
    // 为每种题型进行随机组题
    getPaperQuestionIdList(
        qChoiceNum, paperQuestionIdList, SysConsts.QUESTION.CHOICE_TYPE, courseId);
    getPaperQuestionIdList(
        qMulChoiceNum, paperQuestionIdList, SysConsts.QUESTION.MUL_CHOICE_TYPE, courseId);
    getPaperQuestionIdList(qTofNum, paperQuestionIdList, SysConsts.QUESTION.TOF_TYPE, courseId);
    getPaperQuestionIdList(qFillNum, paperQuestionIdList, SysConsts.QUESTION.FILL_TYPE, courseId);
    getPaperQuestionIdList(qSaqNum, paperQuestionIdList, SysConsts.QUESTION.SAQ_TYPE, courseId);
    getPaperQuestionIdList(
        qProgramNum, paperQuestionIdList, SysConsts.QUESTION.PROGRAM_TYPE, courseId);
    // 生成试卷题目序列，Example：（1,2,3,4,5,6,7,8）
    StringBuilder builder = new StringBuilder();
    for (Integer id : paperQuestionIdList) {
      String idStr = String.valueOf(id);
      builder.append(idStr);
      builder.append(StrUtil.COMMA);
    }
    String ids = builder.toString();
    // 去除最后一个逗号并封装题序参数
    paper.setQuestionId(ids.substring(0, ids.length() - 1));
    paperMapper.insert(paper);
  }

  @Override
  public List<PaperForm> findAllPaperForm() {
    return paperFormMapper.selectList(null);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void delPaperFormById(Integer id) {
    PaperForm form = paperFormMapper.selectById(id);
    if (form == null) {
      throw new ServiceException("试卷模版不存在!");
    }
    // 查找是否有正在使用该模版的试卷，如果有，则不允许删除模版
    QueryWrapper<Paper> qw = new QueryWrapper<>();
    qw.lambda().eq(Paper::getPaperFormId, id);
    List<Paper> papers = paperMapper.selectList(qw);
    if (CollUtil.isNotEmpty(papers)) {
      throw new ServiceException("试卷模版正在使用，不能删除该模版！");
    }
    paperFormMapper.deleteById(id);
  }

  @Override
  public List<Paper> findPracticePapersByMajorId(Integer majorId) {
    // 构造条件询条件
    QueryWrapper<Paper> qw = new QueryWrapper<>();
    qw.lambda().eq(Paper::getMajorId, majorId);
    // 只查询模拟考试
    qw.lambda().eq(Paper::getPaperType, SysConsts.PAPER.PAPER_TYPE_PRACTICE);
    return paperMapper.selectList(qw);
  }

  @Override
  public Set<Question> findQuestionsByPaperIdAndType(Integer paperId, Integer qChoiceType) {
    // 通过 ID 查询试卷信息
    Paper paper = paperMapper.selectById(paperId);
    // 获取试卷的题目序号集合，Example:（1,2,3,4,5,6,7）
    String qIds = paper.getQuestionId();
    // 分割题目序号
    String[] qIdArray = StrUtil.splitToArray(qIds, StrUtil.C_COMMA);
    Set<Question> questionSet = Sets.newHashSet();
    for (String id : qIdArray) {
      // 通过题目 ID 获取问题的信息
      Question question = questionMapper.selectById(id);
      if (qChoiceType.equals(question.getTypeId())) {
        questionSet.add(question);
      }
    }
    return questionSet;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public String markPaper(Integer stuId, Integer paperId, HttpServletRequest request)
      throws ServiceException {
    // 通过 ID 获取试卷信息
    Paper paper = paperMapper.selectById(paperId);
    // 通过 ID 获取试卷模板信息
    PaperForm paperForm = paperFormMapper.selectById(paper.getPaperFormId());
    // 获取试卷中各个部分题型的问题信息
    Set<Question> qChoiceList =
        findQuestionsByPaperIdAndType(paperId, SysConsts.QUESTION.CHOICE_TYPE);
    Set<Question> qMulChoiceList =
        findQuestionsByPaperIdAndType(paperId, SysConsts.QUESTION.MUL_CHOICE_TYPE);
    Set<Question> qTofList = findQuestionsByPaperIdAndType(paperId, SysConsts.QUESTION.TOF_TYPE);
    Set<Question> qFillList = findQuestionsByPaperIdAndType(paperId, SysConsts.QUESTION.FILL_TYPE);
    Set<Question> qSaqList = findQuestionsByPaperIdAndType(paperId, SysConsts.QUESTION.SAQ_TYPE);
    Set<Question> qProgramList =
        findQuestionsByPaperIdAndType(paperId, SysConsts.QUESTION.PROGRAM_TYPE);
    // 获取模板各个题型的题目分值
    int qChoiceScore = NumberUtil.strToInteger(paperForm.getQChoiceNum());
    int qMulChoiceScore = NumberUtil.strToInteger(paperForm.getQMulChoiceScore());
    int qTofScore = NumberUtil.strToInteger(paperForm.getQTofScore());
    int qFillScore = NumberUtil.strToInteger(paperForm.getQFillScore());
    double qSaqScore = NumberUtil.strToDouble(paperForm.getQSaqScore());
    double qProgramScore = NumberUtil.strToDouble(paperForm.getQProgramScore());
    // 错题集
    List<String> wrongIds = Lists.newArrayList();
    // 定义默认分值
    int score = 0;
    /* -------------------------- 开始评分 -------------------------- */
    // 单选题批改
    MarkInfoDto qChoiceMark = PaperMarkUtil.mark(qChoiceList, qChoiceScore, request);
    score += qChoiceMark.getScore();
    wrongIds.addAll(qChoiceMark.getWrongIds());
    // 多选题批改
    MarkInfoDto qMulChoiceMark = PaperMarkUtil.mulMark(qMulChoiceList, qMulChoiceScore, request);
    score += qMulChoiceMark.getScore();
    wrongIds.addAll(qMulChoiceMark.getWrongIds());
    // 判断题批改
    MarkInfoDto qTofMark = PaperMarkUtil.mark(qTofList, qTofScore, request);
    score += qTofMark.getScore();
    wrongIds.addAll(qTofMark.getWrongIds());
    // 填空题批改
    MarkInfoDto qFillMark = PaperMarkUtil.mark(qFillList, qFillScore, request);
    score += qFillMark.getScore();
    wrongIds.addAll(qFillMark.getWrongIds());
    // 简答题批改并将答题记录存入数据库
    MarkInfoDto essayMark = PaperMarkUtil.essayMark(qSaqList, qSaqScore, request);
    score += essayMark.getScore();
    wrongIds.addAll(essayMark.getWrongIds());
    for (StuAnswerRecord record : essayMark.getStuAnswerRecord()) {
      record.setPaperId(paperId);
      record.setStuId(stuId);
      record.setScore(essayMark.getScore());
      stuAnswerRecordMapper.insert(record);
    }
    // 编程题批改
    MarkInfoDto programMark = PaperMarkUtil.essayMark(qProgramList, qProgramScore, request);
    score += programMark.getScore();
    /* -------------------------- 结束评分 -------------------------- */
    StringBuilder builder = new StringBuilder();
    for (String id : wrongIds) {
      builder.append(id);
      builder.append(StrUtil.COMMA);
    }
    String wrong = builder.toString();
    String wrongResIds = wrong.substring(0, wrong.length() - 1);
    Score scoreObj =
        new Score(stuId, paperId, paper.getPaperName(), String.valueOf(score), wrongResIds);
    scoreMapper.insert(scoreObj);
    return String.valueOf(score);
  }

  @Override
  public List<Paper> findUnDoPaperListByTeacherId(Integer id) {
    // 获取当前时间
    String now = DateUtil.getFormatLocalDateTimeStr();
    // 构造条件查询语句
    QueryWrapper<Paper> qw = new QueryWrapper<>();
    qw.lambda().eq(Paper::getTeacherId, id);
    qw.lambda().eq(Paper::getPaperType, SysConsts.PAPER.PAPER_TYPE_FORMAL);
    // 开始时间大于当前时间
    qw.lambda().gt(Paper::getBeginTime, now);
    return paperMapper.selectList(qw);
  }

  @Override
  public void editPaperById(Integer id, Paper paper) {
    System.out.println(paper.toString());
    paper.setId(id);
    paperMapper.updateById(paper);
  }

  @Override
  public List<Paper> findDonePaperListByTeacherId(Integer teacherId) {
    QueryWrapper<Paper> qw = new QueryWrapper<>();
    qw.lambda()
        .eq(Paper::getPaperState, SysConsts.PAPER.PAPER_STATE_END)
        .eq(Paper::getTeacherId, teacherId);
    return paperMapper.selectList(qw);
  }

  @Override
  public List<StuAnswerRecord> findAnswerRecordByStuAndPaper(String stuNumber, Integer paperId)
      throws ServiceException {
    // 通过学号查询学生是否存在
    QueryWrapper<Student> qw = new QueryWrapper<>();
    qw.lambda().eq(Student::getStuNumber, stuNumber);
    Student student = studentMapper.selectOne(qw);
    if (ObjectUtil.isEmpty(student)) {
      throw new ServiceException("该学号不存在！");
    }
    // 构造根据学号和试卷ID条件查询语句
    QueryWrapper<StuAnswerRecord> ansQw = new QueryWrapper<>();
    ansQw.lambda().eq(StuAnswerRecord::getStuId, student.getId());
    ansQw.lambda().eq(StuAnswerRecord::getPaperId, paperId);
    return stuAnswerRecordMapper.selectList(ansQw);
  }

  @Override
  public Major findMajorById(Integer id) {
    return majorMapper.selectById(id);
  }

  @Override
  public void changeStateById(Integer id) {
    Paper paper = paperMapper.selectById(id);
    paper.setPaperState(SysConsts.PAPER.PAPER_STATE_END);
    paperMapper.updateById(paper);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void delPaperById(Integer id) {
    Paper paper = this.paperMapper.selectById(id);
    if (paper != null) {
      Teacher teacher = teacherMapper.selectById(paper.getTeacherId());
      // 删除score表中paperId为传入参数的对象
      QueryWrapper<Score> scoreQw = new QueryWrapper<>();
      scoreQw.lambda().eq(Score::getPaperId, id);
      List<Score> scores = this.scoreMapper.selectList(scoreQw);
      // 通过ID 删除
      scores.forEach(score -> scoreMapper.deleteById(score.getId()));
      // 删除 stu_answer_record
      QueryWrapper<StuAnswerRecord> ansQw = new QueryWrapper<>();
      ansQw.lambda().eq(StuAnswerRecord::getPaperId, id);
      List<StuAnswerRecord> ans = this.stuAnswerRecordMapper.selectList(ansQw);
      // 删除答题记录
      ans.forEach(an -> stuAnswerRecordMapper.deleteById(an.getId()));
      // 删除试卷
      paperMapper.deleteById(id);
      logger.info("教师：{} 删除了试卷：{}", teacher.getName(), paper.getPaperName());
    }
  }

  @Override
  public List<Paper> findPaperByMajorId(Integer majorId) {
    QueryWrapper<Paper> qw = new QueryWrapper<>();
    qw.lambda().eq(Paper::getMajorId, majorId);
    // 只查询正式考试
    qw.lambda().eq(Paper::getPaperType, SysConsts.PAPER.PAPER_TYPE_FORMAL);
    return this.paperMapper.selectList(qw);
  }

  /**
   * 组卷方法
   *
   * @param varQuestionTypeNum 该类型问题的数量
   * @param paperQuestionIdList 试卷问题集合
   * @param questionType 问题类型
   * @param courseId 课程 ID
   */
  private void getPaperQuestionIdList(
      String varQuestionTypeNum,
      List<Integer> paperQuestionIdList,
      Integer questionType,
      Integer courseId) {
    int num;
    // 类型题存在才进行随机抽题
    if (StrUtil.isNotEmpty(varQuestionTypeNum)) {
      // 转整型
      num = Integer.parseInt(varQuestionTypeNum);
      // 获取类型题的 ID 集合
      List<Integer> idList = questionService.getIdList(questionType, courseId);
      // 随机抽题
      List<Integer> qChoiceIdList = getRandomIdList(idList, num);
      paperQuestionIdList.addAll(qChoiceIdList);
    }
  }

  /**
   * 从所需题型中随机抽出固定数量的题（随机组题核心代码）
   *
   * @param ids 题库中该题型所有的题目的id集合
   * @param num 题目数量
   * @return 题目集合
   */
  private List<Integer> getRandomIdList(List<Integer> ids, Integer num) throws ServiceException {
    // 调用随机数生成工具
    Random random = RandomUtil.getRandom();
    List<Integer> result = Lists.newArrayList();
    int index;
    for (int i = 0; i < num; i++) {
      try {
        // 利用题序集合长度-1（集合索引范围）作为随机因子，随机取索引值
        index = random.nextInt(ids.size() - 1);
        // 从题序集合冲获取该索引的题
        result.add(ids.get(index));
        // 移除该题序防止题目重复同时保证题序集合长度在安全范围内进行随机取值
        ids.remove(index);
      } catch (Exception e) {
        // 捕捉题序集合索引溢出异常，说明题目数量不够进行随机组题
        throw new ServiceException("试题数量不足，组卷失败！");
      }
    }
    return result;
  }
}
