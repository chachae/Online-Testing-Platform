package com.chachae.exam.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chachae.exam.common.constant.SysConsts;
import com.chachae.exam.common.dao.PaperDAO;
import com.chachae.exam.common.dao.PaperFormDAO;
import com.chachae.exam.common.dao.StuAnswerRecordDAO;
import com.chachae.exam.common.dao.TypeDAO;
import com.chachae.exam.common.exception.ServiceException;
import com.chachae.exam.common.model.Grade;
import com.chachae.exam.common.model.Paper;
import com.chachae.exam.common.model.PaperForm;
import com.chachae.exam.common.model.Question;
import com.chachae.exam.common.model.Score;
import com.chachae.exam.common.model.StuAnswerRecord;
import com.chachae.exam.common.model.dto.ImportPaperRandomQuestionDto;
import com.chachae.exam.common.model.dto.MarkInfoDto;
import com.chachae.exam.common.model.dto.PaperQuestionUpdateDto;
import com.chachae.exam.common.model.dto.QueryPaperDto;
import com.chachae.exam.common.util.NumberUtil;
import com.chachae.exam.common.util.PageUtil;
import com.chachae.exam.common.util.PaperMarkUtil;
import com.chachae.exam.service.GradeService;
import com.chachae.exam.service.PaperService;
import com.chachae.exam.service.QuestionService;
import com.chachae.exam.service.ScoreService;
import com.google.common.collect.Lists;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 试卷批改业务实现
 *
 * @author chachae
 * @date 2020/2/2
 */
@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class PaperServiceImpl extends ServiceImpl<PaperDAO, Paper> implements PaperService {

  private final PaperDAO paperDAO;
  private final PaperFormDAO paperFormDAO;
  private final QuestionService questionService;
  private final StuAnswerRecordDAO stuAnswerRecordDAO;
  private final ScoreService scoreService;
  private final TypeDAO typeDAO;
  private final GradeService gradeService;

  @Override
  public void randomNewPaper(Paper paper, String diff) {
    // 获取试卷模板信息
    PaperForm form = paperFormDAO.selectById(paper.getPaperFormId());
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
   * @param qs    问题ID集合
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
  @Transactional(rollbackFor = Exception.class)
  public void markPaper(Integer stuId, Integer paperId, HttpServletRequest request) {
    // 通过 ID 获取试卷信息
    Paper paper = paperDAO.selectById(paperId);
    // 试卷名称
    String paperName = paper.getPaperName();
    // 通过 ID 获取试卷模板信息
    PaperForm paperForm = paperFormDAO.selectById(paper.getPaperFormId());

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

    // 获取试卷中各个部分题型的问题信息
    List<Question> choices = questionService.selectByPaperIdAndType(paperId, 1);
    List<Question> mulChoices = questionService.selectByPaperIdAndType(paperId, 2);
    List<Question> tofs = questionService.selectByPaperIdAndType(paperId, 3);
    List<Question> fills = questionService.selectByPaperIdAndType(paperId, 4);
    List<Question> saqs = questionService.selectByPaperIdAndType(paperId, 5);
    List<Question> programs = questionService.selectByPaperIdAndType(paperId, 6);

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
        stuAnswerRecordDAO.insert(record);
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
    this.scoreService.save(scoreResult);
  }

  @Override
  public List<Paper> listDoneByTeacherId(Integer teacherId) {
    // 构造通过教师ID查询已经完成的试卷信息
    LambdaQueryWrapper<Paper> qw = new LambdaQueryWrapper<>();
    qw.eq(Paper::getPaperState, SysConsts.Paper.PAPER_STATE_END);
    qw.eq(Paper::getTeacherId, teacherId);
    return paperDAO.selectList(qw);
  }

  @Override
  public boolean updateById(Paper paper) {
    // 通过起止时间计算考试时长
    if (paper.getPaperType().equals(SysConsts.Paper.PAPER_TYPE_FORMAL)) {
      if (paper.getBeginTime() != null && paper.getEndTime() != null) {
        paper.setAllowTime(calAllowTime(paper.getBeginTime(), paper.getEndTime()));
      }
    } else {
      paper.setBeginTime(null);
      paper.setEndTime(null);
    }
    baseMapper.updateById(paper);
    return true;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deletePaperById(Integer id) {
    // 查询试卷是否存在
    Paper paper = this.paperDAO.selectById(id);
    if (paper != null) {
      // 检查试卷是否在考试时间范围内，是的话不允许被删除（结束的话可以被删除/模拟考可以直接删除）
      if (paper.getPaperType().equals(SysConsts.Paper.PAPER_TYPE_FORMAL)
          // 已经开始
          && paper.isStart()
          // 还未结束
          && !paper.isEnd()) {
        throw new ServiceException("考试已开始，无法删除！");
      }

      // 删除score表中paperId为传入参数的对象
      LambdaQueryWrapper<Score> scoreQw = new LambdaQueryWrapper<>();
      scoreQw.eq(Score::getPaperId, id);
      List<Score> scores = this.scoreService.list(scoreQw);
      // 遍历成绩集合，并逐一删除对应试卷的成绩数据
      scores.forEach(score -> scoreService.removeById(score.getId()));

      // 删除学生与该试卷关联的答题记录
      LambdaQueryWrapper<StuAnswerRecord> ansQw = new LambdaQueryWrapper<>();
      // 构造查询条件
      ansQw.eq(StuAnswerRecord::getPaperId, id);
      List<StuAnswerRecord> ans = this.stuAnswerRecordDAO.selectList(ansQw);
      // 遍历删除答题记录
      ans.forEach(an -> stuAnswerRecordDAO.deleteById(an.getId()));

      // 获取试卷模板，如果只有他使用，则进行删除
      int paperFormId = paper.getPaperFormId();
      // 排除默认模板
      if (paperFormId != 1) {
        // 如果数量等于 1，说明只有本考试使用，直接删除
        if (this.countPaperByPaperFormId(paperFormId) == 1) {
          this.paperFormDAO.deleteById(paperFormId);
        }
      }

      // 删除试卷
      paperDAO.deleteById(id);
    }
  }

  @Override
  public List<Paper> selectByMajorId(Integer majorId) {
    LambdaQueryWrapper<Paper> qw = new LambdaQueryWrapper<>();
    qw.eq(Paper::getMajorId, majorId);
    // 只查询正式考试
    qw.eq(Paper::getPaperType, SysConsts.Paper.PAPER_TYPE_FORMAL);
    return this.paperDAO.selectList(qw);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void updateQuestionId(PaperQuestionUpdateDto dto) {
    // 查询试卷信息
    Paper paper = this.paperDAO.selectById(dto.getPaperId());
    if (paper == null) {
      throw new ServiceException("试卷不存在");
    }

    // 查询新题目信息
    Question newQuestion = this.questionService.getById(dto.getNewId());
    if (newQuestion == null) {
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
    this.paperDAO.updateById(Paper.builder().id(dto.getPaperId()).questionId(ids).build());
  }

  @Override
  public List<Paper> listByTeacherId(Integer teacherId) {
    LambdaQueryWrapper<Paper> qw = new LambdaQueryWrapper<>();
    qw.eq(Paper::getTeacherId, teacherId);
    return this.paperDAO.selectList(qw);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void saveWithImportPaper(Paper paper, ImportPaperRandomQuestionDto entity) {
    // 获取试卷模板信息
    Integer paperFormId = paper.getPaperFormId();
    PaperForm form = this.paperFormDAO.selectById(paperFormId);
    // 判断是否存在已存在提醒仍进行随机抽题的情况
    if (Integer.parseInt(form.getQChoiceNum()) > 0 && entity.getA() == 1) {
      throw new ServiceException("试卷中已存在 [ 单项选择题 ]，请取消随机抽题后重试！");
    }
    if (Integer.parseInt(form.getQMulChoiceNum()) > 0 && entity.getB() == 1) {
      throw new ServiceException("试卷中已存在 [ 多项选择题 ]，请取消随机抽题后重试！");
    }
    if (Integer.parseInt(form.getQTofNum()) > 0 && entity.getC() == 1) {
      throw new ServiceException("试卷中已存在 [ 判断题 ]，请取消随机抽题后重试！");
    }
    if (Integer.parseInt(form.getQFillNum()) > 0 && entity.getD() == 1) {
      throw new ServiceException("试卷中已存在 [ 填空题 ]，请取消随机抽题后重试！");
    }
    if (Integer.parseInt(form.getQSaqNum()) > 0 && entity.getE() == 1) {
      throw new ServiceException("试卷中已存在 [ 主观题 ]，请取消随机抽题后重试！");
    }
    if (Integer.parseInt(form.getQProgramNum()) > 0 && entity.getF() == 1) {
      throw new ServiceException("试卷中已存在 [ 编程题 ]，请取消随机抽题后重试！");
    }

    // 新建一个 ID 集合，后续进行拼接
    List<Integer> idList = Lists.newArrayList();
    // 获取试卷所属课程ID
    Integer courseId = paper.getCourseId();

    // 至此，随机抽题请求合法，进行分布随机
    // 单项选择
    if (entity.getA() == 1) {
      // 设置模板信息
      form.setQChoiceNum(entity.getANum());
      form.setQChoiceScore(entity.getAScore());
      // 判断试题难度
      if (entity.getADif().equals(SysConsts.Diff.AVG)) {
        // 不需要过滤难度
        this.randomQuestions(entity.getANum(), idList, 1, courseId, null);
      } else {
        this.randomQuestions(entity.getANum(), idList, 1, courseId, entity.getADif());
      }
    }

    // 多项选择
    if (entity.getB() == 1) {
      // 设置模板信息
      form.setQMulChoiceNum(entity.getBNum());
      form.setQMulChoiceScore(entity.getBScore());
      // 判断试题难度
      if (entity.getBDif().equals(SysConsts.Diff.AVG)) {
        this.randomQuestions(entity.getBNum(), idList, 2, courseId, null);
      } else {
        this.randomQuestions(entity.getBNum(), idList, 2, courseId, entity.getBDif());
      }
    }

    // 判断题
    if (entity.getC() == 1) {
      // 设置模板信息
      form.setQTofNum(entity.getCNum());
      form.setQTofScore(entity.getCScore());
      // 判断试题难度
      if (entity.getCDif().equals(SysConsts.Diff.AVG)) {
        this.randomQuestions(entity.getCNum(), idList, 3, courseId, null);
      } else {
        this.randomQuestions(entity.getCNum(), idList, 3, courseId, entity.getCDif());
      }
    }

    // 填空题
    if (entity.getD() == 1) {
      // 设置模板信息
      form.setQFillNum(entity.getDNum());
      form.setQFillScore(entity.getDScore());
      // 判断试题难度
      if (entity.getDDif().equals(SysConsts.Diff.AVG)) {
        this.randomQuestions(entity.getDNum(), idList, 4, courseId, null);
      } else {
        this.randomQuestions(entity.getDNum(), idList, 4, courseId, entity.getDDif());
      }
    }

    // 主观题
    if (entity.getE() == 1) {
      // 设置模板信息
      form.setQSaqNum(entity.getENum());
      form.setQSaqScore(entity.getEScore());
      // 判断试题难度
      if (entity.getEDif().equals(SysConsts.Diff.AVG)) {
        this.randomQuestions(entity.getENum(), idList, 5, courseId, null);
      } else {
        this.randomQuestions(entity.getENum(), idList, 5, courseId, entity.getEDif());
      }
    }

    // 编程题
    if (entity.getF() == 1) {
      // 设置模板信息
      form.setQProgramNum(entity.getFNum());
      form.setQProgramScore(entity.getFScore());
      // 判断试题难度
      if (entity.getFDif().equals(SysConsts.Diff.AVG)) {
        this.randomQuestions(entity.getFNum(), idList, 6, courseId, null);
      } else {
        this.randomQuestions(entity.getFNum(), idList, 6, courseId, entity.getFDif());
      }
    }

    // 至此，完成随机抽题，更新试卷模板
    this.paperFormDAO.updateById(form);
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
    LambdaQueryWrapper<Paper> qw = new LambdaQueryWrapper<>();
    qw.eq(Paper::getPaperFormId, paperFormId);
    return this.paperDAO.selectCount(qw);
  }

  @Override
  public Map<String, Object> pagePaper(Page<Paper> page, QueryPaperDto entity) {
    LambdaQueryWrapper<Paper> qw = new LambdaQueryWrapper<>();

    // 试卷类型
    if (StrUtil.isNotBlank(entity.getPaperType())) {
      qw.eq(Paper::getPaperType, entity.getPaperType());
    }

    // 所属专业
    if (entity.getMajorId() != null) {
      qw.eq(Paper::getMajorId, entity.getMajorId());
    }

    // 所属学院
    if (entity.getAcademyId() != null) {
      qw.eq(Paper::getAcademyId, entity.getAcademyId());
    }

    // 出卷老师
    if (entity.getTeacherId() != null) {
      qw.eq(Paper::getTeacherId, entity.getTeacherId());
    }

    // 所属课程
    if (entity.getCourseId() != null) {
      qw.eq(Paper::getCourseId, entity.getCourseId());
    }

    // 试卷名称
    if (StrUtil.isNotBlank(entity.getPaperName())) {
      qw.like(Paper::getPaperName, entity.getPaperName());
    }

    // 试卷年级
    if (entity.getLevel() != null) {
      qw.eq(Paper::getLevel, entity.getLevel());
    }

    Page<Paper> pageInfo = this.paperDAO.selectPage(page, qw);
    // 班级
    if (entity.getGradeId() != null) {
      if (pageInfo.getTotal() != 0L) {
        List<Paper> records = pageInfo.getRecords();
        for (int i = 0; i < records.size(); i++) {
          List<String> gids = StrUtil.split(records.get(i).getGradeIds(), ',');
          if (!gids.contains(String.valueOf(entity.getGradeId()))) {
            records.remove(i);
            pageInfo.setTotal(pageInfo.getTotal() - 1L);
          }
        }
      }
    }
    return PageUtil.toPage(pageInfo);
  }

  @Override
  public void updateGradeIds(Paper paper) {
    if (paper.getLevel() == null || StrUtil.isBlank(paper.getGradeIds())) {
      throw new ServiceException("请填写年级和班级信息");
    }
    List<Grade> grades = this.gradeService.listByMajorId(paper.getMajorId());
    if (CollUtil.isEmpty(grades)) {
      throw new ServiceException("专业不存在班级，请添加班级");
    }
    List<Integer> numbers = grades.stream().map(Grade::getGradeNumber)
        .collect(Collectors.toList());
    // 获取班级id
    String[] gIds = StrUtil.splitToArray(paper.getGradeIds(), ',');
    for (String gId : gIds) {
      if (!numbers.contains(Integer.parseInt(gId))) {
        throw new ServiceException("班级" + gId + "不存在");
      }
    }
    this.paperDAO.updateById(paper);
  }

  @Override
  public void checkTestedByGradeId(Integer paperId, Integer level, Integer gradeId) {
    Paper paper = getById(paperId);
    List<String> idList = StrUtil.split(paper.getGradeIds(), ',');
    Grade grade = gradeService.getById(gradeId);
    if (!paper.getLevel().equals(level) || !idList
        .contains(String.valueOf(grade.getGradeNumber()))) {
      throw new ServiceException("该考试不属于该班级");
    }

    List<Score> scores = this.scoreService.selectByPaperIdAndGradeId(paperId, gradeId);
    if (CollUtil.isEmpty(scores)) {
      throw new ServiceException("该班级没有考试记录");
    }

  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean save(Paper entity) {

    // 将模拟试卷的日期移除掉
    if (entity.getPaperType().equals(SysConsts.Paper.PAPER_TYPE_PRACTICE)) {
      entity.setBeginTime(null);
      entity.setEndTime(null);
    }

    // 正式考试才要计算起止时间
    if (entity.getPaperType().equals(SysConsts.Paper.PAPER_TYPE_FORMAL)) {
      // 計计算起止时间
      String allowTime = calAllowTime(entity.getBeginTime(), entity.getEndTime());
      // 封装时常
      entity.setAllowTime(allowTime);
    }
    baseMapper.insert(entity);
    return true;
  }

  /**
   * 指定难度的随机抽题方法
   *
   * @param typeNum 试题数量
   * @param qIds    试题的 ID 集合
   * @param tid     试题类型
   * @param cid     课程 ID
   * @param dif     难度（传入null代表难度不限定）
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
      // 过滤难度（diff为null 说明不需要过滤难度）
      if (StrUtil.isNotBlank(dif)) {
        qs = qs.stream().filter(q -> q.getDifficulty().equals(dif)).collect(Collectors.toList());
      }
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
        index = bound <= 1 ? 0 : random.nextInt(bound - 1);
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
          String typeName = this.typeDAO.selectById(typeId).getTypeName();
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
   * @param endTime   结束时间
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
    LocalDateTime d1 = LocalDateTime.parse(beginTime, DateTimeFormatter.ofPattern(pattern));
    LocalDateTime d2 = LocalDateTime.parse(endTime, DateTimeFormatter.ofPattern(pattern));

    // 判断起止时间是否符合要求
    if (d1.isBefore(LocalDateTime.now())) {
      throw new ServiceException("请设置一个未来的时间");
    } else if (d1.isAfter(d2)) {
      throw new ServiceException("开始时间不能晚于结束时间！");
    }

    // 判断时间间隔
    if (d1.getDayOfMonth() != d2.getDayOfMonth()) {
      throw new ServiceException("请合理设置考试时间 [ 禁止设置间隔不在同一天内的时间！ ]");
    }

    // 封装时间
    return ((d2.getHour() - d1.getHour()) * 60 + (d2.getMinute() - d1.getMinute())) + "分钟";
  }
}
