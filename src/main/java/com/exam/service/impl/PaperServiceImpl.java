package com.exam.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exam.constant.SysConsts;
import com.exam.entity.*;
import com.exam.entity.dto.MarkInfoDto;
import com.exam.entity.dto.PaperQuestionUpdateDto;
import com.exam.exception.ServiceException;
import com.exam.mapper.PaperFormMapper;
import com.exam.mapper.PaperMapper;
import com.exam.mapper.ScoreMapper;
import com.exam.mapper.StuAnswerRecordMapper;
import com.exam.service.PaperService;
import com.exam.service.QuestionService;
import com.exam.util.DateUtil;
import com.exam.util.NumberUtil;
import com.exam.util.PaperMarkUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 试卷批改业务实现
 *
 * @author yzn
 * @date 2020/2/2
 */
@Slf4j
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class PaperServiceImpl extends ServiceImpl<PaperMapper, Paper> implements PaperService {

  @Resource private PaperMapper paperMapper;
  @Resource private PaperFormMapper paperFormMapper;
  @Resource private QuestionService questionService;
  @Resource private StuAnswerRecordMapper stuAnswerRecordMapper;
  @Resource private ScoreMapper scoreMapper;

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
  public void randomNewPaper(Paper paper) {
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
    List<Integer> paperQuestions = Lists.newArrayList();
    // 为每种题型进行随机组题
    randomQuestions(qChoiceNum, paperQuestions, SysConsts.QUESTION.CHOICE_TYPE, courseId);
    randomQuestions(qMulChoiceNum, paperQuestions, SysConsts.QUESTION.MUL_CHOICE_TYPE, courseId);
    randomQuestions(qTofNum, paperQuestions, SysConsts.QUESTION.TOF_TYPE, courseId);
    randomQuestions(qFillNum, paperQuestions, SysConsts.QUESTION.FILL_TYPE, courseId);
    randomQuestions(qSaqNum, paperQuestions, SysConsts.QUESTION.SAQ_TYPE, courseId);
    randomQuestions(qProgramNum, paperQuestions, SysConsts.QUESTION.PROGRAM_TYPE, courseId);
    // 生成试卷题目序列，Example：（1,2,3,4,5,6,7,8）
    savePaper(paper, paperQuestions);
  }

  @Override
  public void randomNewPaper(Paper paper, String difficulty) {
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
    List<Integer> qs = Lists.newArrayList();
    // 为每种题型进行随机组题
    randomQsWithDif(qChoiceNum, qs, SysConsts.QUESTION.CHOICE_TYPE, courseId, difficulty);
    randomQsWithDif(qMulChoiceNum, qs, SysConsts.QUESTION.MUL_CHOICE_TYPE, courseId, difficulty);
    randomQsWithDif(qTofNum, qs, SysConsts.QUESTION.TOF_TYPE, courseId, difficulty);
    randomQsWithDif(qFillNum, qs, SysConsts.QUESTION.FILL_TYPE, courseId, difficulty);
    randomQsWithDif(qSaqNum, qs, SysConsts.QUESTION.SAQ_TYPE, courseId, difficulty);
    randomQsWithDif(qProgramNum, qs, SysConsts.QUESTION.PROGRAM_TYPE, courseId, difficulty);
    // 生成试卷题目序列，Example：（1,2,3,4,5,6,7,8）
    savePaper(paper, qs);
  }

  /**
   * 试卷保存行为
   *
   * @param paper 试卷
   * @param qs 问题ID集合
   */
  private void savePaper(Paper paper, List<Integer> qs) {
    StringBuilder sb = new StringBuilder();
    // 通过循环的方式组件试卷题目序号集合
    qs.forEach(id -> sb.append(id).append(StrUtil.COMMA));
    String ids = sb.toString();
    // 去除最后一个逗号并封装题序参数
    paper.setQuestionId(ids.substring(0, ids.length() - 1));
    // 将试卷信息插入 paper 表中
    this.save(paper);
  }

  @Override
  public List<Paper> selectPracticePapersByMajorId(Integer majorId) {
    // 构造条件询条件
    QueryWrapper<Paper> qw = new QueryWrapper<>();
    qw.lambda().eq(Paper::getMajorId, majorId);
    // 只查询模拟考试
    qw.lambda().eq(Paper::getPaperType, SysConsts.PAPER.PAPER_TYPE_PRACTICE);
    return paperMapper.selectList(qw);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void markPaper(Integer stuId, Integer paperId, HttpServletRequest request) {
    // 通过 ID 获取试卷信息
    Paper paper = paperMapper.selectById(paperId);
    // 通过 ID 获取试卷模板信息
    PaperForm paperForm = paperFormMapper.selectById(paper.getPaperFormId());
    // 获取试卷中各个部分题型的问题信息
    Set<Question> qChoiceList =
        questionService.selectByPaperIdAndType(paperId, SysConsts.QUESTION.CHOICE_TYPE);
    Set<Question> qMulChoiceList =
        questionService.selectByPaperIdAndType(paperId, SysConsts.QUESTION.MUL_CHOICE_TYPE);
    Set<Question> qTofList =
        questionService.selectByPaperIdAndType(paperId, SysConsts.QUESTION.TOF_TYPE);
    Set<Question> qFillList =
        questionService.selectByPaperIdAndType(paperId, SysConsts.QUESTION.FILL_TYPE);
    Set<Question> qSaqList =
        questionService.selectByPaperIdAndType(paperId, SysConsts.QUESTION.SAQ_TYPE);
    Set<Question> qProgramList =
        questionService.selectByPaperIdAndType(paperId, SysConsts.QUESTION.PROGRAM_TYPE);
    // 获取模板各个题型的题目分值
    int qChoiceScore = NumberUtil.strToInteger(paperForm.getQChoiceScore());
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
    // 通过循环的方式依次将主观题的错题信息插入学生答题记录表中
    for (StuAnswerRecord record : essayMark.getStuAnswerRecord()) {
      // 封装学生、试卷、分数信息
      record.setPaperId(paperId);
      record.setStuId(stuId);
      stuAnswerRecordMapper.insert(record);
    }
    // 编程题批改
    MarkInfoDto programMark = PaperMarkUtil.essayMark(qProgramList, qProgramScore, request);
    score += programMark.getScore();
    /* -------------------------- 结束评分 -------------------------- */

    // 组装错题集合信息
    StringBuilder builder = new StringBuilder();
    wrongIds.forEach(id -> builder.append(id).append(StrUtil.COMMA));
    // 和上面一样将最后一个逗号去除
    String wrong = builder.toString();
    String wrongResIds = wrong.substring(0, wrong.length() - 1);
    // 封装分数参数，并将分数信息插入到分数表中
    Score scoreObj =
        new Score(stuId, paperId, paper.getPaperName(), String.valueOf(score), wrongResIds);
    // 此处调用插入接口
    this.scoreMapper.insert(scoreObj);
  }

  @Override
  public List<Paper> listUnDoByTeacherId(Integer id) {
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
  public void updateById(Integer id, Paper paper) {
    paper.setId(id);
    paper.setAllowTime(calAllowTime(paper.getBeginTime(), paper.getEndTime()));
    this.updateById(paper);
  }

  @Override
  public List<Paper> listDoneByTeacherId(Integer teacherId) {
    // 构造通过教师ID查询已经完成的试卷信息
    QueryWrapper<Paper> qw = new QueryWrapper<>();
    qw.lambda().eq(Paper::getPaperState, SysConsts.PAPER.PAPER_STATE_END);
    qw.lambda().eq(Paper::getTeacherId, teacherId);
    return paperMapper.selectList(qw);
  }

  @Override
  public void updateStateById(Integer id) {
    // 更新试卷的状态
    Paper paper = paperMapper.selectById(id);
    paper.setPaperState(SysConsts.PAPER.PAPER_STATE_END);
    paperMapper.updateById(paper);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void delPaperById(Integer id) {
    // 查询试卷是否存在
    Paper paper = this.paperMapper.selectById(id);
    if (paper != null) {
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
    }
  }

  @Override
  public List<Paper> selectByMajorId(Integer majorId) {
    QueryWrapper<Paper> qw = new QueryWrapper<>();
    qw.lambda().eq(Paper::getMajorId, majorId);
    // 只查询正式考试
    qw.lambda().eq(Paper::getPaperType, SysConsts.PAPER.PAPER_TYPE_FORMAL);
    return this.paperMapper.selectList(qw);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void updateQuestionId(PaperQuestionUpdateDto dto) {
    // 查询试卷信息
    Paper paper = this.paperMapper.selectById(dto.getPaperId());
    if (ObjectUtil.isEmpty(paper)) {
      throw new ServiceException("试卷不存在");
    }
    // 查询新题目信息
    Question newQuestion = this.questionService.getById(dto.getNewId());
    if (ObjectUtil.isEmpty(newQuestion)) {
      throw new ServiceException("新题目不存在");
    }
    // 查询就题目信息，匹配信息
    Question oldQuestion = this.questionService.getById(dto.getOldId());
    if (!newQuestion.getTypeId().equals(oldQuestion.getTypeId())) {
      throw new ServiceException("新题目类型与旧题目类型不匹配");
    }
    if (!newQuestion.getCourseId().equals(oldQuestion.getCourseId())) {
      throw new ServiceException("新题目类型与旧题目所属课程不匹配");
    }
    // 更新试卷题目ID
    String ids = paper.getQuestionId();
    String[] idStr = StrUtil.splitToArray(ids, StrUtil.C_COMMA);
    // 转 String 数据
    // 组装错题集合信息
    StringBuilder builder = new StringBuilder();
    for (String s : idStr) {
      if (s.equals(String.valueOf(dto.getOldId()))) {
        builder.append(dto.getNewId()).append(StrUtil.COMMA);
      } else {
        builder.append(s).append(StrUtil.COMMA);
      }
    }
    // 和上面一样将最后一个逗号去除
    ids = builder.toString();
    ids = ids.substring(0, ids.length() - 1);
    // 插入新数据
    Paper newPaper = new Paper();
    newPaper.setId(dto.getPaperId());
    newPaper.setQuestionId(ids);
    this.paperMapper.updateById(newPaper);
  }

  @Override
  public List<Paper> listByTeacherId(Integer teacherId) {
    QueryWrapper<Paper> qw = new QueryWrapper<>();
    qw.lambda().eq(Paper::getTeacherId,teacherId);
    return this.paperMapper.selectList(qw);
  }

  /**
   * 计算所属课程+所属试题类型的试题数量
   *
   * @param varQuestionTypeNum 该类型问题的数量
   * @param paperQuestionIdList 试卷问题集合
   * @param questionType 问题类型
   * @param courseId 课程 ID
   */
  private void randomQuestions(
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
      List<Question> questions = questionService.listByTypeIdAndCourseId(questionType, courseId);
      List<Integer> idList = Lists.newArrayList();
      // 循环问题集合获取问题 ID，将 ID 加入idList 中
      questions.forEach(question -> idList.add(question.getId()));
      // 随机抽题
      List<Integer> qChoiceIdList = getRandomIdList(idList, num);
      paperQuestionIdList.addAll(qChoiceIdList);
    }
  }

  private void randomQsWithDif(
      String varQuestionTypeNum,
      List<Integer> paperQuestionIdList,
      Integer questionType,
      Integer courseId,
      String dif) {
    int num;
    // 类型题存在才进行随机抽题
    if (StrUtil.isNotEmpty(varQuestionTypeNum)) {
      // 转整型
      num = Integer.parseInt(varQuestionTypeNum);
      // 获取类型题的 ID 集合
      List<Question> qs = questionService.listByTypeIdAndCourseId(questionType, courseId);
      // 过滤难度
      qs = qs.stream().filter(q -> q.getDifficulty().equals(dif)).collect(Collectors.toList());
      List<Integer> idList = Lists.newArrayList();
      // 循环问题集合获取问题 ID，将 ID 加入idList 中
      qs.forEach(question -> idList.add(question.getId()));
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
  private List<Integer> getRandomIdList(List<Integer> ids, Integer num) {
    // 调用随机数生成工具
    Random random = RandomUtil.getRandom();
    List<Integer> result = Lists.newArrayList();
    int index;
    for (int i = 0; i < num; i++) {
      try {
        // 利用题序集合长度-1（集合索引范围）作为随机因子，随机取索引值
        int bound = ids.size();
        if (bound <= 1) {
          index = 0;
        } else {
          index = random.nextInt(bound - 1);
        }
        // 从题序集合中获取该索引的题
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

  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean save(Paper entity) {
    // 正式考试才要计算起止时间
    if (entity.getPaperType().equals(SysConsts.PAPER.PAPER_TYPE_FORMAL)) {
      // 計算起止时间
      String allowTime = calAllowTime(entity.getBeginTime(), entity.getEndTime());
      entity.setAllowTime(allowTime);
    }
    return super.save(entity);
  }

  /**
   * 计算考试起止时间
   *
   * @param beginTime 开始时间
   * @param endTime 结束时间
   * @return 时间长度
   */
  private String calAllowTime(String beginTime, String endTime) {
    // 計算试卷起止时间
    final String pattern = "yyyy-MM-dd HH:mm";
    DateTime d1 = DateUtil.getDateTime(beginTime, pattern);
    DateTime d2 = DateUtil.getDateTime(endTime, pattern);
    StringBuilder build = StrUtil.builder();
    // 封装时间
    return build.append(d2.getMinuteOfDay() - d1.getMinuteOfDay()).append("分钟").toString();
  }
}
