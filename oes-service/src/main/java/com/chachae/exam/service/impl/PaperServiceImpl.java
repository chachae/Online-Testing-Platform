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
import com.chachae.exam.util.service.ExcelTemplateService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

  private final TypeDAO typeDAO;
  private final PaperDAO paperDAO;
  private final GradeService gradeService;
  private final PaperFormDAO paperFormDAO;
  private final ScoreService scoreService;
  private final QuestionService questionService;
  private final StuAnswerRecordDAO stuAnswerRecordDAO;
  private final ExcelTemplateService excelTemplateService;

  @Override
  public void randomNewPaper(Paper paper, String diff) {
    // 获取试卷模板信息
    PaperForm form = paperFormDAO.selectById(paper.getPaperFormId());
    // 获取试卷归属的课程 ID
    int courseId = paper.getCourseId();
    // 预先准备试卷问题集合
    List<Integer> questionIds = new ArrayList<>();
    // 为每种题型进行随机组题
    String[] questionNumArray = {form.getQChoiceNum(), form.getQMulChoiceNum(), form.getQTofNum(),
        form.getQFillNum(), form.getQSaqNum(), form.getQProgramNum()};
    for (int i = 0; i < questionNumArray.length; i++) {
      this.randomQuestions(questionNumArray[i], questionIds, i + 1, courseId, diff);
    }
    this.savePaper(paper, questionIds);
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
    List<String> wrongIds = new ArrayList<>();
    // 定义默认分值
    int score = 0;

    /* -------------------------- 开始评分 -------------------------- */
    // 加同步锁
    synchronized (this) {
      // 获取试卷中全部部分题型的问题信息
      List<Question> questions = questionService.selectByPaperIdAndType(paperId, null);
      // 试题集合按照类型排序+分组
      Collection<List<Question>> collection = questions.stream()
          .sorted(Comparator.comparingInt(Question::getTypeId))
          .collect(Collectors.groupingBy(Question::getTypeId)).values();

      for (List<Question> questionList : collection) {
        switch (questionList.get(0).getTypeId()) {
          case (1):
            // 单选题批改
            MarkInfoDto choiceMark = PaperMarkUtil.mark(questionList, choiceScore, request);
            score += choiceMark.getScore();
            wrongIds.addAll(choiceMark.getWrongIds());
            break;
          case (2):
            // 多选题批改
            MarkInfoDto mulChoiceMark = PaperMarkUtil
                .mulMark(questionList, mulChoiceScore, request);
            score += mulChoiceMark.getScore();
            wrongIds.addAll(mulChoiceMark.getWrongIds());
            break;
          case (3):
            // 判断题批改
            MarkInfoDto tofMark = PaperMarkUtil.mark(questionList, tofScore, request);
            score += tofMark.getScore();
            wrongIds.addAll(tofMark.getWrongIds());
            break;
          case (4):
            // 填空题批改
            MarkInfoDto fillMark = PaperMarkUtil.mark(questionList, fillScore, request);
            score += fillMark.getScore();
            wrongIds.addAll(fillMark.getWrongIds());
            break;
          case (5):
            // 简答题批改并将答题记录存入数据库
            MarkInfoDto essayMark = PaperMarkUtil.essayMark(questionList, saqScore, request);
            score += essayMark.getScore();
            wrongIds.addAll(essayMark.getWrongIds());
            // 通过循环的方式依次将主观题的错题信息插入学生答题记录表中
            for (StuAnswerRecord record : essayMark.getStuAnswerRecord()) {
              // 封装学生、试卷、分数信息
              record.setPaperId(paperId).setStuId(stuId);
              stuAnswerRecordDAO.insert(record);
            }
            break;
          default:
            // 编程题批改
            MarkInfoDto programMark = PaperMarkUtil.essayMark(questionList, programScore, request);
            score += programMark.getScore();
            wrongIds.addAll(programMark.getWrongIds());
            break;
        }
      }
    }

    /* -------------------------- 结束评分 -------------------------- */

    // 组装错题集合信息
    StringBuilder builder = new StringBuilder();
    for (String id : wrongIds) {
      builder.append(id).append(',');
    }
    // 最后一个逗号去除
    String wrong = builder.toString();
    // 预备一个空错题字符串
    String wrongStr = null;
    // 如果没有错题，就直赋值空，长度大于0就说明包含错题
    if (wrong.length() > 0) {
      wrongStr = wrong.substring(0, wrong.length() - 1);
    }

    // 封装分数参数，并将分数信息插入到分数表中
    Score scoreResult = new Score(stuId, paperId, paperName, String.valueOf(score), wrongStr);
    // 此处调用插入接口
    this.scoreService.save(scoreResult);
  }

  @Override
  public List<Paper> listDoneByTeacherId(Integer teacherId) {
    // 构造通过教师ID查询已经完成的试卷信息
    LambdaQueryWrapper<Paper> qw = new LambdaQueryWrapper<>();
    qw.eq(Paper::getPaperState, SysConsts.Paper.PAPER_STATE_END);
    if (teacherId != null) {
      qw.eq(Paper::getTeacherId, teacherId);
    }
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
      // 检查试卷是否在考试时间范围内，是的话不允许被删除（模拟考可以直接删除）
      if (paper.getPaperType().equals(SysConsts.Paper.PAPER_TYPE_FORMAL)
          // 已经开始
          && paper.isStart()) {
        throw new ServiceException("考试已开始或已结束，无法删除！");
      } else {
        // 模拟考试可以删除
        // 删除score表中paperId为传入参数的对象
        List<Score> scores = this.scoreService.selectByPaperId(id);
        // 遍历成绩集合，并逐一删除对应试卷的成绩数据
        scores.forEach(score -> scoreService.removeById(score.getId()));

        // 删除学生与该试卷关联的答题记录， 构造查询条件
        LambdaQueryWrapper<StuAnswerRecord> ansQw = new LambdaQueryWrapper<>();
        ansQw.eq(StuAnswerRecord::getPaperId, id);
        List<StuAnswerRecord> ans = this.stuAnswerRecordDAO.selectList(ansQw);
        // 遍历删除答题记录
        ans.forEach(an -> stuAnswerRecordDAO.deleteById(an.getId()));
        // 获取试卷模板，如果只有他使用，则进行删除
        int paperFormId = paper.getPaperFormId();
        // 排除默认模板，且如果数量等于 1，说明只有本考试使用，直接删除
        if (paperFormId != 1 && this.countPaperByPaperFormId(paperFormId) == 1) {
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
    List<String> idList = StrUtil.split(ids, ',');
    if (idList.contains(String.valueOf(dto.getNewId()))) {
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
    String[] idStr = StrUtil.splitToArray(ids, ',');
    // 使用 StringBuilder 拼接 ID
    StringBuilder builder = new StringBuilder();
    for (String s : idStr) {
      if (s.equals(String.valueOf(dto.getOldId()))) {
        // 如果 ID 跟被替换的 ID 相同，则凭借新的题目 ID
        builder.append(dto.getNewId()).append(',');
      } else {
        // 反之直接拼接
        builder.append(s).append(',');
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
    // 模板题目数量
    String[] formNums = {form.getQChoiceNum(), form.getQMulChoiceNum(), form.getQTofNum(),
        form.getQFillNum(), form.getQSaqNum(), form.getQProgramNum()};
    // 设置的具体题型随机选择情况
    boolean[] isOpen = {entity.getA() == 1, entity.getB() == 1, entity.getC() == 1,
        entity.getD() == 1, entity.getE() == 1, entity.getF() == 1};
    // 判断是否有重复题型
    // 新建一个 ID 集合，后续进行拼接
    List<Integer> idList = new ArrayList<>();
    // 获取试卷所属课程ID
    int courseId = paper.getCourseId();
    for (int i = 0; i < formNums.length; i++) {
      // 符合局部随机抽题要求
      if (Integer.parseInt(formNums[i]) == 0 && isOpen[i]) {
        switch (i) {
          case (0):
            // 单选
            form.setQChoiceNum(entity.getANum()).setQChoiceScore(entity.getAScore());
            this.randomQuestions(entity.getANum(), idList, i + 1, courseId, entity.getADif());
            break;
          case (1):
            // 多项选择
            form.setQMulChoiceNum(entity.getBNum()).setQMulChoiceScore(entity.getBScore());
            this.randomQuestions(entity.getBNum(), idList, i + 1, courseId, entity.getBDif());
            break;
          case (2):
            // 判断题
            form.setQTofNum(entity.getCNum()).setQTofScore(entity.getCScore());
            this.randomQuestions(entity.getCNum(), idList, i + 1, courseId, entity.getCDif());
            break;
          case (3):
            // 填空题
            form.setQFillNum(entity.getDNum()).setQFillScore(entity.getDScore());
            this.randomQuestions(entity.getDNum(), idList, i + 1, courseId, entity.getDDif());
            break;
          case (4):
            // 主观题
            form.setQSaqNum(entity.getENum()).setQSaqScore(entity.getEScore());
            this.randomQuestions(entity.getENum(), idList, i + 1, courseId, entity.getEDif());
            break;
          default:
            // 编程题
            form.setQProgramNum(entity.getFNum()).setQProgramScore(entity.getFScore());
            this.randomQuestions(entity.getFNum(), idList, i + 1, courseId, entity.getFDif());
            break;
        }
      } else if (Integer.parseInt(formNums[i]) > 0 && isOpen[i]) {
        throw new ServiceException("请取消已经存在试卷内的题型随机抽取选项后重试！");
      }
    }

    // 至此，完成随机抽题，更新试卷模板
    this.paperFormDAO.updateById(form);
    // 拼接 ID，获取试卷原本的试题集合 ID
    List<String> idStr = StrUtil.split(paper.getQuestionId(), ',');
    for (Integer e : idList) {
      idStr.add(String.valueOf(e));
    }
    // 获取新的ID
    // 建立 StringBuilder 对象，用户组装试题集合
    StringBuilder sb = new StringBuilder();
    // 拼接试题 ID 和 逗号
    idStr.forEach(id -> sb.append(id).append(','));
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
    // 班级不为空，数据也不为0
    if (entity.getGradeId() != null && pageInfo.getTotal() != 0L) {
      List<Paper> records = pageInfo.getRecords();
      // 迭代器内使用remove()，不要使用for循环
      Iterator<Paper> iterator = records.iterator();
      while (iterator.hasNext()) {
        Paper paper = iterator.next();
        List<String> gIds = StrUtil.split(paper.getGradeIds(), ',');
        if (!gIds.contains(String.valueOf(entity.getGradeId()))) {
          iterator.remove();
          pageInfo.setTotal(pageInfo.getTotal() - 1L);
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
    if (StrUtil.isBlank(paper.getGradeIds())) {
      throw new ServiceException("试卷未指派班级");
    }
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
  public void outputPaperExcel(Integer paperId, HttpServletResponse response) {
    Paper paper = baseMapper.selectById(paperId);
    String[] typeNameArray = {"单项选择题", "多项选择题", "判断题", "填空题", "主观题", "编程题"};
    List<Question> questions = this.questionService.selectByPaperIdAndType(paperId, null);
    // 设置题目类型名称
    for (Question question : questions) {
      question.setTypeName(typeNameArray[question.getTypeId() - 1]);
    }
    excelTemplateService.packingPaper(paper, questions, response);
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
   * @param typeNum     试题数量
   * @param questionIds 试题的 ID 集合
   * @param typeId      试题类型
   * @param courseId    课程 ID
   * @param dif         难度（传入null代表难度不限定）
   */
  private void randomQuestions(
      String typeNum, List<Integer> questionIds, Integer typeId, Integer courseId, String dif) {
    // 初始值
    int num;
    // 类型题存在才进行随机抽题
    if (StrUtil.isNotEmpty(typeNum)) {
      // 转整型
      num = Integer.parseInt(typeNum);
      // 获取类型题的 ID 集合
      List<Question> qs = questionService.listByTypeIdAndCourseId(typeId, courseId);
      // 过滤难度（diff为null或者0说明不需要过滤难度）
      if (StrUtil.isNotBlank(dif) && Integer.parseInt(dif) != 0) {
        qs = qs.stream().filter(q -> q.getDifficulty().equals(dif)).collect(Collectors.toList());
      }
      List<Integer> idList = new ArrayList<>();
      // 遍历问题集合获取问题 ID，将 ID 加入idList 中
      for (Question question : qs) {
        idList.add(question.getId());
      }
      // 随机抽题
      List<Integer> randomIds = getRandomIdList(idList, num);
      questionIds.addAll(randomIds);
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
    // 获取随机数生成器对象
    Random random = RandomUtil.getRandom();
    List<Integer> result = new ArrayList<>();
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
   * 试卷保存行为
   *
   * @param paper 试卷
   * @param qs    问题ID集合
   */
  private void savePaper(Paper paper, List<Integer> qs) {
    StringBuilder sb = new StringBuilder();
    // 通过循环的方式组件试卷题目序号集合
    for (Integer id : qs) {
      sb.append(id).append(',');
    }
    String ids = sb.toString();
    // 去除最后一个逗号并封装题序参数
    paper.setQuestionId(ids.substring(0, ids.length() - 1));
    // 将试卷信息插入 paper 表中
    this.save(paper);
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
