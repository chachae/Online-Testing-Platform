package com.exam.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exam.constant.SysConsts;
import com.exam.entity.*;
import com.exam.entity.dto.ImportPaperRandomQuestionDto;
import com.exam.entity.dto.MarkInfoDto;
import com.exam.entity.dto.PaperQuestionUpdateDto;
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
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class PaperServiceImpl extends ServiceImpl<PaperMapper, Paper> implements PaperService {

  @Resource private PaperMapper paperMapper;
  @Resource private PaperFormMapper paperFormMapper;
  @Resource private QuestionService questionService;
  @Resource private StuAnswerRecordMapper stuAnswerRecordMapper;
  @Resource private ScoreMapper scoreMapper;
  @Resource private TypeMapper typeMapper;

  @Override
  public PageInfo<Paper> pageForPaperList(Integer teacherId, Integer pageNo) {
    // 构造条件询条件
    QueryWrapper<Paper> qw = new QueryWrapper<>();
    qw.lambda().eq(Paper::getTeacherId, teacherId);
    // 分页查询，默认每页 12 条数据
    PageHelper.startPage(pageNo, 12);
    List<Paper> paperList = paperMapper.selectList(qw);
    return new PageInfo<>(paperList);
  }

  @Override
  public void randomNewPaper(Paper paper) {
    // 获取试卷模板信息
    PaperForm form = paperFormMapper.selectById(paper.getPaperFormId());
    // 获取试卷归属的课程 ID
    Integer cid = paper.getCourseId();
    // 预先准备试卷问题集合
    List<Integer> qs = Lists.newArrayList();
    // 为每种题型进行随机组题
    this.randomQuestions(form.getQChoiceNum(), qs, 1, cid);
    this.randomQuestions(form.getQMulChoiceNum(), qs, 2, cid);
    this.randomQuestions(form.getQTofNum(), qs, 3, cid);
    this.randomQuestions(form.getQFillNum(), qs, 4, cid);
    this.randomQuestions(form.getQSaqNum(), qs, 5, cid);
    this.randomQuestions(form.getQProgramNum(), qs, 6, cid);
    // 生成试卷题目序列，Example：（1,2,3,4,5,6,7,8）
    this.savePaper(paper, qs);
  }

  @Override
  public void randomNewPaper(Paper paper, String diff) {
    // 获取试卷模板信息
    PaperForm form = paperFormMapper.selectById(paper.getPaperFormId());
    // 获取试卷归属的课程 ID
    Integer cid = paper.getCourseId();
    // 预先准备试卷问题集合
    List<Integer> qs = Lists.newArrayList();
    // 为每种题型进行随机组题
    this.randomQuestions(form.getQChoiceNum(), qs, 1, cid, diff);
    this.randomQuestions(form.getQMulChoiceNum(), qs, 2, cid, diff);
    this.randomQuestions(form.getQTofNum(), qs, 3, cid, diff);
    this.randomQuestions(form.getQFillNum(), qs, 4, cid, diff);
    this.randomQuestions(form.getQSaqNum(), qs, 5, cid, diff);
    this.randomQuestions(form.getQProgramNum(), qs, 6, cid, diff);
    // 生成试卷题目序列，Example：（1,2,3,4,5,6,7,8）
    this.savePaper(paper, qs);
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
    // 试卷名称
    String paperName = paper.getPaperName();
    // 通过 ID 获取试卷模板信息
    PaperForm paperForm = paperFormMapper.selectById(paper.getPaperFormId());
    // 获取试卷中各个部分题型的问题信息
    Set<Question> choices = questionService.selectByPaperIdAndType(paperId, 1);
    Set<Question> mulChoices = questionService.selectByPaperIdAndType(paperId, 2);
    Set<Question> tofs = questionService.selectByPaperIdAndType(paperId, 3);
    Set<Question> fills = questionService.selectByPaperIdAndType(paperId, 4);
    Set<Question> saqs = questionService.selectByPaperIdAndType(paperId, 5);
    Set<Question> programs = questionService.selectByPaperIdAndType(paperId, 6);

    // 获取模板各个题型的题目分值
    int choiceScore = NumberUtil.strToInteger(paperForm.getQChoiceScore());
    int mulChoiceScore = NumberUtil.strToInteger(paperForm.getQMulChoiceScore());
    int tofScore = NumberUtil.strToInteger(paperForm.getQTofScore());
    int fillScore = NumberUtil.strToInteger(paperForm.getQFillScore());
    double saqScore = NumberUtil.strToDouble(paperForm.getQSaqScore());
    double programScore = NumberUtil.strToDouble(paperForm.getQProgramScore());

    // 错题集
    List<String> wrongIds = Lists.newArrayList();
    // 定义默认分值
    int score = 0;

    /* -------------------------- 开始评分 -------------------------- */
    // 单选题批改
    MarkInfoDto choiceMark = PaperMarkUtil.mark(choices, choiceScore, request);
    score += choiceMark.getScore();
    wrongIds.addAll(choiceMark.getWrongIds());

    // 多选题批改
    MarkInfoDto mulChoiceMark = PaperMarkUtil.mulMark(mulChoices, mulChoiceScore, request);
    score += mulChoiceMark.getScore();
    wrongIds.addAll(mulChoiceMark.getWrongIds());

    // 判断题批改
    MarkInfoDto tofMark = PaperMarkUtil.mark(tofs, tofScore, request);
    score += tofMark.getScore();
    wrongIds.addAll(tofMark.getWrongIds());

    // 填空题批改
    MarkInfoDto fillMark = PaperMarkUtil.mark(fills, fillScore, request);
    score += fillMark.getScore();
    wrongIds.addAll(fillMark.getWrongIds());

    // 简答题批改并将答题记录存入数据库
    MarkInfoDto essayMark = PaperMarkUtil.essayMark(saqs, saqScore, request);
    score += essayMark.getScore();
    wrongIds.addAll(essayMark.getWrongIds());

    // 编程题批改
    MarkInfoDto programMark = PaperMarkUtil.essayMark(programs, programScore, request);
    score += programMark.getScore();
    wrongIds.addAll(programMark.getWrongIds());

    // 加同步锁
    synchronized (this) {
      // 通过循环的方式依次将主观题的错题信息插入学生答题记录表中
      for (StuAnswerRecord record : essayMark.getStuAnswerRecord()) {
        // 封装学生、试卷、分数信息
        record.setPaperId(paperId).setStuId(stuId);
        stuAnswerRecordMapper.insert(record);
      }
    }

    /* -------------------------- 结束评分 -------------------------- */

    // 组装错题集合信息
    StringBuilder builder = new StringBuilder();
    wrongIds.forEach(id -> builder.append(id).append(StrUtil.COMMA));
    // 最后一个逗号去除
    String wrong = builder.toString();
    // 预备一个空错题字符串
    String wstr = null;
    // 如果没有错题，就直赋值空，长度大于0就说明包含错题
    if (wrong.length() > 0) {
      wstr = wrong.substring(0, wrong.length() - 1);
    }

    // 封装分数参数，并将分数信息插入到分数表中
    Score scoreResult = new Score(stuId, paperId, paperName, String.valueOf(score), wstr);
    // 此处调用插入接口
    this.scoreMapper.insert(scoreResult);
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
  public List<Paper> listDoneByTeacherId(Integer teacherId) {
    // 构造通过教师ID查询已经完成的试卷信息
    QueryWrapper<Paper> qw = new QueryWrapper<>();
    qw.lambda().eq(Paper::getPaperState, SysConsts.PAPER.PAPER_STATE_END);
    qw.lambda().eq(Paper::getTeacherId, teacherId);
    return paperMapper.selectList(qw);
  }

  @Override
  public boolean updateById(Paper paper) {
    // 通过起止时间计算考试时长
    if (paper.getPaperType().equals(SysConsts.PAPER.PAPER_TYPE_FORMAL)) {
      paper.setAllowTime(calAllowTime(paper.getBeginTime(), paper.getEndTime()));
    } else {
      paper.setBeginTime(null);
      paper.setEndTime(null);
    }
    return super.updateById(paper);
  }

  @Override
  public void updateStateById(Integer id) {
    // 更新试卷的状态
    Paper paper = paperMapper.selectById(id);
    // 更新状态为 [结束状态]
    paper.setPaperState(SysConsts.PAPER.PAPER_STATE_END);
    paperMapper.updateById(paper);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deletePaperById(Integer id) {
    // 查询试卷是否存在
    Paper paper = this.paperMapper.selectById(id);
    if (ObjectUtil.isNotEmpty(paper)) {
      // 检查试卷是否在考试时间范围内，是的话不允许被删除（结束的话可以被删除/模拟考可以直接删除）
      if (paper.getPaperType().equals(SysConsts.PAPER.PAPER_TYPE_FORMAL)
          // 已经开始
          && paper.isStart()
          // 还未结束
          && !paper.isEnd()) {
        throw new ServiceException("考试已开始，无法删除！");
      }

      // 删除score表中paperId为传入参数的对象
      QueryWrapper<Score> scoreQw = new QueryWrapper<>();
      scoreQw.lambda().eq(Score::getPaperId, id);
      List<Score> scores = this.scoreMapper.selectList(scoreQw);
      // 遍历成绩集合，并逐一删除对应试卷的成绩数据
      scores.forEach(score -> scoreMapper.deleteById(score.getId()));

      // 删除学生与该试卷关联的答题记录
      QueryWrapper<StuAnswerRecord> ansQw = new QueryWrapper<>();
      // 构造查询条件
      ansQw.lambda().eq(StuAnswerRecord::getPaperId, id);
      List<StuAnswerRecord> ans = this.stuAnswerRecordMapper.selectList(ansQw);
      // 遍历删除答题记录
      ans.forEach(an -> stuAnswerRecordMapper.deleteById(an.getId()));

      // 获取试卷模板，如果只有他使用，则进行删除
      int paperFormId = paper.getPaperFormId();
      // 排除默认模板
      if (paperFormId != 1) {
        // 如果数量等于 1，说明只有本考试使用，直接删除
        if (this.countPaperByPaperFormId(paperFormId) == 1) {
          this.paperFormMapper.deleteById(paperFormId);
        }
      }

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

    // 更新试卷题目ID
    String ids = paper.getQuestionId();
    if (StrUtil.split(ids, StrUtil.C_COMMA).contains(String.valueOf(dto.getNewId()))) {
      throw new ServiceException("新题目试卷中已存在");
    }

    // 查询旧题目信息，进行信息的匹配
    Question oldQuestion = this.questionService.getById(dto.getOldId());
    // 题型匹配
    if (!newQuestion.getTypeId().equals(oldQuestion.getTypeId())) {
      throw new ServiceException("新题目类型与旧题目类型不匹配");
    }

    // 所属课程的 ID 匹配
    if (!newQuestion.getCourseId().equals(oldQuestion.getCourseId())) {
      throw new ServiceException("新题目类型与旧题目所属课程不匹配");
    }

    // 用逗号分割转字符数组
    String[] idStr = StrUtil.splitToArray(ids, StrUtil.C_COMMA);
    // 使用 StringBuilder 拼接 ID
    StringBuilder builder = new StringBuilder();
    for (String s : idStr) {
      if (s.equals(String.valueOf(dto.getOldId()))) {
        // 如果 ID 跟被替换的 ID 相同，则凭借新的题目 ID
        builder.append(dto.getNewId()).append(StrUtil.COMMA);
      } else {
        // 反之直接拼接
        builder.append(s).append(StrUtil.COMMA);
      }
    }
    // 将字符串的最后一个逗号去除
    ids = builder.toString();
    ids = ids.substring(0, ids.length() - 1);

    // 插入新数据
    this.paperMapper.updateById(Paper.builder().id(dto.getPaperId()).questionId(ids).build());
  }

  @Override
  public List<Paper> listByTeacherId(Integer teacherId) {
    QueryWrapper<Paper> qw = new QueryWrapper<>();
    qw.lambda().eq(Paper::getTeacherId, teacherId);
    return this.paperMapper.selectList(qw);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void saveWithImportPaper(Paper paper, ImportPaperRandomQuestionDto entity) {
    // 获取试卷模板信息
    Integer paperFormId = paper.getPaperFormId();
    PaperForm form = this.paperFormMapper.selectById(paperFormId);

    // 预备随机开启值
    // 开启单选题
    boolean isChoiceOn = entity.getA() == 1,
        // 开启多选题
        isMulChoiceOn = entity.getB() == 1,
        // 开启判断题
        isTofOn = entity.getC() == 1,
        // 开启填空题
        isFillOn = entity.getD() == 1,
        // 开启主观题
        isSaqOn = entity.getE() == 1,
        // 开启编程题
        isProgramOn = entity.getF() == 1;

    // 判断是否存在已存在提醒仍进行随机抽题的情况
    if (Integer.parseInt(form.getQChoiceNum()) > 0 && isChoiceOn) {
      throw new ServiceException("试卷中已存在 [ 单项选择题 ]，请取消随机抽题后重试！");
    }
    if (Integer.parseInt(form.getQMulChoiceNum()) > 0 && isMulChoiceOn) {
      throw new ServiceException("试卷中已存在 [ 多项选择题 ]，请取消随机抽题后重试！");
    }
    if (Integer.parseInt(form.getQTofNum()) > 0 && isTofOn) {
      throw new ServiceException("试卷中已存在 [ 判断题 ]，请取消随机抽题后重试！");
    }
    if (Integer.parseInt(form.getQFillNum()) > 0 && isFillOn) {
      throw new ServiceException("试卷中已存在 [ 填空题 ]，请取消随机抽题后重试！");
    }
    if (Integer.parseInt(form.getQSaqNum()) > 0 && isSaqOn) {
      throw new ServiceException("试卷中已存在 [ 主观题 ]，请取消随机抽题后重试！");
    }
    if (Integer.parseInt(form.getQProgramNum()) > 0 && isProgramOn) {
      throw new ServiceException("试卷中已存在 [ 编程题 ]，请取消随机抽题后重试！");
    }

    // 新建一个 ID 集合，后续进行拼接
    List<Integer> idList = Lists.newArrayList();
    // 获取试卷所属课程ID
    Integer courseId = paper.getCourseId();

    // 至此，随机抽题请求合法，进行分布随机
    // 单项选择
    if (isChoiceOn) {
      // 设置模板信息
      form.setQChoiceNum(entity.getANum());
      form.setQChoiceScore(entity.getAScore());
      // 判断试题难度
      if (entity.getADif().equals(SysConsts.Diff.AVG)) {
        // 不需要过滤难度
        this.randomQuestions(entity.getANum(), idList, 1, courseId);
      } else {
        this.randomQuestions(entity.getANum(), idList, 1, courseId, entity.getADif());
      }
    }

    // 多项选择
    if (isMulChoiceOn) {
      // 设置模板信息
      form.setQMulChoiceNum(entity.getBNum());
      form.setQMulChoiceScore(entity.getBScore());
      // 判断试题难度
      if (entity.getBDif().equals(SysConsts.Diff.AVG)) {
        this.randomQuestions(entity.getBNum(), idList, 2, courseId);
      } else {
        this.randomQuestions(entity.getBNum(), idList, 2, courseId, entity.getBDif());
      }
    }

    // 判断题
    if (isTofOn) {
      // 设置模板信息
      form.setQTofNum(entity.getCNum());
      form.setQTofScore(entity.getCScore());
      // 判断试题难度
      if (entity.getCDif().equals(SysConsts.Diff.AVG)) {
        this.randomQuestions(entity.getCNum(), idList, 3, courseId);
      } else {
        this.randomQuestions(entity.getCNum(), idList, 3, courseId, entity.getCDif());
      }
    }

    // 填空题
    if (isFillOn) {
      // 设置模板信息
      form.setQFillNum(entity.getDNum());
      form.setQFillScore(entity.getDScore());
      // 判断试题难度
      if (entity.getDDif().equals(SysConsts.Diff.AVG)) {
        this.randomQuestions(entity.getDNum(), idList, 4, courseId);
      } else {
        this.randomQuestions(entity.getDNum(), idList, 4, courseId, entity.getDDif());
      }
    }

    // 主观题
    if (isSaqOn) {
      // 设置模板信息
      form.setQSaqNum(entity.getENum());
      form.setQSaqScore(entity.getEScore());
      // 判断试题难度
      if (entity.getEDif().equals(SysConsts.Diff.AVG)) {
        this.randomQuestions(entity.getENum(), idList, 5, courseId);
      } else {
        this.randomQuestions(entity.getENum(), idList, 5, courseId, entity.getEDif());
      }
    }

    // 编程题
    if (isProgramOn) {
      // 设置模板信息
      form.setQProgramNum(entity.getFNum());
      form.setQProgramScore(entity.getFScore());
      // 判断试题难度
      if (entity.getFDif().equals(SysConsts.Diff.AVG)) {
        this.randomQuestions(entity.getFNum(), idList, 6, courseId);
      } else {
        this.randomQuestions(entity.getFNum(), idList, 6, courseId, entity.getFDif());
      }
    }

    // 至此，完成随机抽题，更新试卷模板
    this.paperFormMapper.updateById(form);
    // 拼接 ID，获取试卷原本的试题集合 ID
    List<String> idStr = StrUtil.split(paper.getQuestionId(), StrUtil.C_COMMA);
    idList.forEach(e -> idStr.add(String.valueOf(e)));
    // 获取新的ID
    // 建立 StringBuilder 对象，用户组装试题集合
    StringBuilder sb = new StringBuilder();
    // 拼接试题 ID 和 逗号
    idStr.forEach(id -> sb.append(id).append(StrUtil.COMMA));
    String ids = sb.toString();
    // 去除最后一个逗号并封装题序参数
    paper.setQuestionId(ids.substring(0, ids.length() - 1));
    // 插入试卷信息
    this.save(paper);
  }

  @Override
  public int countPaperByPaperFormId(Integer paperFormId) {
    // 构造通过试卷模板查询试卷数量条件
    QueryWrapper<Paper> qw = new QueryWrapper<>();
    qw.lambda().eq(Paper::getPaperFormId, paperFormId);
    return this.paperMapper.selectCount(qw);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean save(Paper entity) {

    // 将模拟试卷的日期移除掉
    if (entity.getPaperType().equals(SysConsts.PAPER.PAPER_TYPE_PRACTICE)) {
      entity.setBeginTime(null);
      entity.setEndTime(null);
    }

    // 正式考试才要计算起止时间
    if (entity.getPaperType().equals(SysConsts.PAPER.PAPER_TYPE_FORMAL)) {
      // 計计算起止时间
      String allowTime = calAllowTime(entity.getBeginTime(), entity.getEndTime());
      // 封装时常
      entity.setAllowTime(allowTime);
    }
    return super.save(entity);
  }

  /**
   * 计算所属课程+所属试题类型的试题数量
   *
   * @param typeNum 该类型问题的数量
   * @param qIds 试卷问题的 ID 集合
   * @param tid 问题类型
   * @param cid 课程 ID
   */
  private void randomQuestions(String typeNum, List<Integer> qIds, Integer tid, Integer cid) {
    // 初始值
    int num;
    // 类型题存在才进行随机抽题
    if (StrUtil.isNotEmpty(typeNum)) {
      // 转整型
      num = Integer.parseInt(typeNum);
      // 获取类型题的 ID 集合
      List<Question> qs = questionService.listByTypeIdAndCourseId(tid, cid);
      List<Integer> idList = Lists.newArrayList();
      // 遍历问题集合获取问题 ID，将 ID 加入idList 中
      qs.forEach(question -> idList.add(question.getId()));
      // 随机抽题
      List<Integer> randomIds = getRandomIdList(idList, num);
      // 封装 ID
      qIds.addAll(randomIds);
    }
  }

  /**
   * 指定难度的随机抽题方法
   *
   * @param typeNum 试题数量
   * @param qIds 试题的 ID 集合
   * @param tid 试题类型
   * @param cid 课程 ID
   * @param dif 难度
   */
  private void randomQuestions(
      String typeNum, List<Integer> qIds, Integer tid, Integer cid, String dif) {
    // 初始值
    int num;
    // 类型题存在才进行随机抽题
    if (StrUtil.isNotEmpty(typeNum)) {
      // 转整型
      num = Integer.parseInt(typeNum);
      // 获取类型题的 ID 集合
      List<Question> qs = questionService.listByTypeIdAndCourseId(tid, cid);
      // 过滤难度
      qs = qs.stream().filter(q -> q.getDifficulty().equals(dif)).collect(Collectors.toList());
      List<Integer> idList = Lists.newArrayList();
      // 遍历问题集合获取问题 ID，将 ID 加入idList 中
      for (Question question : qs) {
        idList.add(question.getId());
      }
      // 随机抽题
      List<Integer> randomIds = getRandomIdList(idList, num);
      qIds.addAll(randomIds);
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
        // 对于空集合，直接跑一场
        if (CollUtil.isEmpty(ids)) {
          // 捕捉题序集合索引溢出异常，说明题目数量不够进行随机组题
          throw new ServiceException("试题数量不足，组卷失败！[ 请增加题目或调整难度后重试 ]");
        } else {
          // 不为空则查询提醒，便于定位具体哪些题目数量不足
          Integer typeId = this.questionService.getById(ids.get(0)).getTypeId();
          // 获取类型名称
          String typeName = this.typeMapper.selectById(typeId).getTypeName();
          throw new ServiceException("该门课程的 [ " + typeName + " ] 数量不足，请增加题目或调整难度后重试");
        }
      }
    }
    return result;
  }

  /**
   * 计算考试起止时间
   *
   * @param beginTime 开始时间
   * @param endTime 结束时间
   * @return 时间长度
   */
  private String calAllowTime(String beginTime, String endTime) {

    // 判断时间是否为空
    if (StrUtil.isBlank(beginTime) || StrUtil.isBlank(endTime)) {
      throw new ServiceException("请设置考试的起止时间！");
    }

    // 检查时间是否合法
    int rightLen = 16;
    if (beginTime.length() != rightLen || endTime.length() != rightLen) {
      throw new ServiceException("时间格式不合法，请在时间选择框中选择！");
    }

    // 計算试卷起止时间
    final String pattern = "yyyy-MM-dd HH:mm";
    DateTime d1 = DateUtil.getDateTime(beginTime, pattern);
    DateTime d2 = DateUtil.getDateTime(endTime, pattern);

    // 判断起止时间是否符合要求
    if (d1.isBeforeNow()) {
      throw new ServiceException("请设置一个未来的时间");
    }
    if (d1.isAfter(d2)) {
      throw new ServiceException("开始时间不能晚于结束时间！");
    }

    // 判断时间间隔
    if (d1.getDayOfMonth() != d2.getDayOfMonth()) {
      throw new ServiceException("请合理设置考试时间 [ 禁止设置间隔不在同一天内的时间！ ]");
    }

    // 封装时间
    StringBuilder build = StrUtil.builder();
    return build.append(d2.getMinuteOfDay() - d1.getMinuteOfDay()).append("分钟").toString();
  }
}
