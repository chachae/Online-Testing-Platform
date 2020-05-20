package com.chachae.exam.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chachae.exam.common.dao.CourseDAO;
import com.chachae.exam.common.dao.GradeDAO;
import com.chachae.exam.common.dao.MajorDAO;
import com.chachae.exam.common.dao.PaperDAO;
import com.chachae.exam.common.dao.ScoreDAO;
import com.chachae.exam.common.dao.StudentDAO;
import com.chachae.exam.common.dao.TeacherDAO;
import com.chachae.exam.common.model.Course;
import com.chachae.exam.common.model.Paper;
import com.chachae.exam.common.model.Score;
import com.chachae.exam.common.model.Teacher;
import com.chachae.exam.common.model.dto.AnswerEditDto;
import com.chachae.exam.common.model.vo.GradeVo;
import com.chachae.exam.common.model.vo.MajorVo;
import com.chachae.exam.common.util.PageUtil;
import com.chachae.exam.service.CourseService;
import com.chachae.exam.service.ScoreService;
import com.chachae.exam.util.service.ExcelTemplateService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 分数业务实现
 *
 * @author chachae
 * @date 2020/1/7
 */
@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class ScoreServiceImpl extends ServiceImpl<ScoreDAO, Score> implements ScoreService {

  private final ScoreDAO scoreDAO;
  private final PaperDAO paperDAO;
  private final CourseDAO courseDAO;
  private final GradeDAO gradeDAO;
  private final StudentDAO studentDAO;
  private final MajorDAO majorDAO;
  private final TeacherDAO teacherDAO;
  private final CourseService courseService;
  private final ExcelTemplateService excelTemplateService;

  @Override
  public List<Score> selectByStuId(Integer id) {
    // QueryWrapper 条件构造器构造查询 Sql
    LambdaQueryWrapper<Score> qw = new LambdaQueryWrapper<>();
    qw.eq(Score::getStuId, id);
    // 返回该学生的分数集合
    return scoreDAO.selectList(qw);
  }

  @Override
  public Map<String, Object> pageByStuId(Page<Score> page, Integer stuId) {
    // QueryWrapper 条件构造器构造查询 Sql
    LambdaQueryWrapper<Score> qw = new LambdaQueryWrapper<>();
    qw.eq(Score::getStuId, stuId);
    // 返回该学生的分数集合
    Page<Score> pageInfo = scoreDAO.selectPage(page, qw);
    return PageUtil.toPage(pageInfo);
  }

  @Override
  public List<Map<String, Object>> averageScore(Integer id) {
    // 通过学生ID查询该学生的成绩 List 集合
    List<Score> scoreList = this.selectByStuId(id);
    // 准备存储平均分 Map 集合
    Map<String, Object> avgMap = Maps.newHashMap();
    // 准备存储学生各科成绩 Map 集合
    Map<String, Object> myScoreMap = Maps.newHashMap();
    // 准备存储各科目平均分 List 集合
    List<Map<String, Object>> avgList = Lists.newArrayList();
    // 统计每门课程平均成绩
    for (Score s : scoreList) {
      // 通过试卷 ID 查询该试卷的平均分
      double avg = scoreDAO.avgScoreByPaperId(s.getPaperId());
      // 通过试卷 ID 查询试卷信息
      Paper paper = paperDAO.selectById(s.getPaperId());
      // 通过课程 ID 查询课程信息
      Course course = courseDAO.selectById(paper.getCourseId());
      // 存入平均分
      avgMap.put(course.getCourseName(), String.valueOf(avg));
    }
    // 统计我的每门课程成绩
    for (Score s : scoreList) {
      String scoreMy = s.getScore();
      // 通过 ID 查询试卷信息
      Paper paper = paperDAO.selectById(s.getPaperId());
      // 通过课程 ID 查询课程信息
      Course course = courseDAO.selectById(paper.getCourseId());
      // 存入我的成绩 Map 集合
      myScoreMap.put(course.getCourseName(), scoreMy);
    }
    // 将平均分 Map 集合和我的成绩 Map 集合存入总的 avgList 集合
    avgList.add(avgMap);
    avgList.add(myScoreMap);
    // 返回结果
    return avgList;
  }

  @Override
  public List<Map<String, Object>> countByLevel(Integer studentId) {
    // 获取该学生的分数 List 集合
    List<Score> scoreList = this.selectByStuId(studentId);

    // 预备用于计算各科成绩等级分布数量 E,D,C,B,A
    long eNum = 0L;
    long dNum = 0L;
    long cNum = 0L;
    long bNum = 0L;
    long aNum = 0L;

    // 预备用于拼接各科成绩等级分布信息的 StringBuilder 对象
    StringBuilder sbe = new StringBuilder();
    StringBuilder sbd = new StringBuilder();
    StringBuilder sbc = new StringBuilder();
    StringBuilder sbb = new StringBuilder();
    StringBuilder sba = new StringBuilder();

    // 预备用于存储分数、等级、结果的集合
    Map<String, Object> scoreMap = Maps.newHashMap();
    Map<String, Object> levelPaperMap = Maps.newHashMap();
    List<Map<String, Object>> resList = Lists.newArrayList();

    // 预备数据的起止符（分别是："[" 和 "] "）
    String startBracket = StrUtil.BRACKET_START;
    String endBracket = StrUtil.BRACKET_END + StrUtil.SPACE;

    // 循环该学生的分数 List 集合并计算出各门考试成绩分数分布信息
    for (Score s : scoreList) {
      // 取除数
      int mdn = Integer.parseInt(s.getScore()) / 10;
      switch (mdn) {
        case 5:
        case 4:
        case 3:
        case 2:
        case 1:
        case 0:
          eNum++;
          sbe.append(startBracket).append(s.getPaperName()).append(endBracket);
          break;
        case 6:
          bNum++;
          sbd.append(startBracket).append(s.getPaperName()).append(endBracket);
          break;
        case 7:
          cNum++;
          sbc.append(startBracket).append(s.getPaperName()).append(endBracket);
          break;
        case 8:
          bNum++;
          sbb.append(startBracket).append(s.getPaperName()).append(endBracket);
          break;
        default:
          aNum++;
          sba.append(startBracket).append(s.getPaperName()).append(endBracket);
          break;
      }
    }

    // 存入分数分布数据
    scoreMap.put("60分以下", eNum);
    scoreMap.put("60-70分", dNum);
    scoreMap.put("70-80分", cNum);
    scoreMap.put("80-90分", bNum);
    scoreMap.put("90分以上", aNum);
    levelPaperMap.put("60分以下", sbe.toString());
    levelPaperMap.put("60-70分", sbd.toString());
    levelPaperMap.put("70-80分", sbc.toString());
    levelPaperMap.put("80-90分", sbb.toString());
    levelPaperMap.put("90分以上", sba.toString());
    resList.add(scoreMap);
    resList.add(levelPaperMap);

    // 返回数据
    return resList;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void updateScoreByStuIdAndPaperId(AnswerEditDto dto) {
    // 获取改门成绩的信息
    LambdaQueryWrapper<Score> qw = new LambdaQueryWrapper<>();
    qw.eq(Score::getStuId, dto.getStuId()).eq(Score::getPaperId, dto.getPaperId());
    Score res = this.scoreDAO.selectOne(qw);
    // 更新成绩
    LambdaUpdateWrapper<Score> uw = new LambdaUpdateWrapper<>();
    uw.eq(Score::getStuId, dto.getStuId()).eq(Score::getPaperId, dto.getPaperId());
    // 新的总成绩
    int score = Integer.parseInt(res.getScore()) - dto.getOldScore() + dto.getNewScore();
    // 构造条件（学生ID+试卷ID）更新 SQL
    uw.set(Score::getScore, score);
    uw.eq(Score::getStuId, dto.getStuId()).eq(Score::getPaperId, dto.getPaperId());
    this.scoreDAO.update(null, uw);
  }

  @Override
  public Score selectByStuIdAndPaperId(Integer stuId, Integer paperId) {
    // 构造学生id和试卷id查询的查询条件
    LambdaQueryWrapper<Score> qw = new LambdaQueryWrapper<>();
    qw.eq(Score::getStuId, stuId).eq(Score::getPaperId, paperId);
    // 返回查询到的数据
    return this.scoreDAO.selectOne(qw);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deleteByStuId(Integer stuId) {
    LambdaQueryWrapper<Score> qw = new LambdaQueryWrapper<>();
    qw.eq(Score::getStuId, stuId);
    this.scoreDAO.delete(qw);
  }

  @Override
  public List<Score> selectByPaperId(Integer paperId) {
    // 构造学生id和试卷id查询的查询条件
    LambdaQueryWrapper<Score> qw = new LambdaQueryWrapper<>();
    qw.eq(Score::getPaperId, paperId);
    // 返回查询到的数据
    return this.scoreDAO.selectList(qw);
  }

  @Override
  public Map<String, Object> averageGradeScore(Integer paperId, Integer gradeId) {
    String scoreKey = "score";
    Map<String, Object> resultMap = new HashMap<>();
    // 设置考试名称
    resultMap.put("title", this.paperDAO.selectById(paperId).getPaperName());
    // "60分以下","60-70分 ","70-80分 ","80-90分 ","90分以上"
    resultMap.put(scoreKey, new int[5]);
    List<Score> scores = selectByPaperId(paperId);
    if (CollUtil.isEmpty(scores)) {
      return resultMap;
    } else {
      // 过滤分数
      // 过滤出该班级的成绩集合
      scores = scores.stream().filter(
          score -> this.studentDAO.selectById(score.getStuId()).getGradeId().equals(gradeId))
          .collect(Collectors.toList());
      if (CollUtil.isEmpty(scores)) {
        return resultMap;
      } else {
        int[] avgs = new int[5];
        // 开始计算
        for (Score score : scores) {
          // 取除数
          int mdn = Integer.parseInt(score.getScore()) / 10;
          switch (mdn) {
            case 5:
            case 4:
            case 3:
            case 2:
            case 1:
            case 0:
              avgs[0]++;
              break;
            case 6:
              avgs[1]++;
              break;
            case 7:
              avgs[2]++;
              break;
            case 8:
              avgs[3]++;
              break;
            default:
              avgs[4]++;
              break;
          }
        }
        resultMap.put(scoreKey, avgs);
      }
    }
    return resultMap;
  }

  @Override
  public void outputPaperChartExcel(Integer paperId, Integer gradeId,
      HttpServletResponse response) {
    // 获取本场考试信息
    Paper paper = this.paperDAO.selectById(paperId);
    // 获取本场考试的的某个班级的分数信息
    List<Score> scores = this.selectByPaperIdAndGradeId(paperId, gradeId);
    // 获取教师信息
    Teacher teacher = this.teacherDAO.selectById(paper.getTeacherId());
    // 获取考试专业信息
    MajorVo major = this.majorDAO.selectVoById(paper.getMajorId());
    // 获取考试班级信息
    GradeVo grade = this.gradeDAO.getVoById(gradeId);
    // 获取考试课程信息
    Course course = this.courseService.getById(paper.getCourseId());
    // 封装参数
    Map<String, Object> sheetMap = new HashMap<>(24);
    // 试卷基础信息
    sheetMap.put("paperName", paper.getPaperName());
    sheetMap.put("academyName", major.getAcademy().getName());
    sheetMap.put("major", major.getMajor());
    sheetMap.put("level", paper.getLevel());
    sheetMap.put("gradeName", grade.getGradeName());
    sheetMap.put("studentCount", scores.size());
    sheetMap.put("courseName", course.getCourseName());
    sheetMap.put("courseAcademyName", course.getAcademy().getName());
    sheetMap.put("courseTeacherName", course.getNames());
    sheetMap.put("teacherName", teacher.getName());

    // 试卷考试分析信息
    //提取分数
    List<String> scoreList = scores.stream().map(Score::getScore).collect(Collectors.toList());
    // 分数总和
    int sum = scoreList.stream().mapToInt(Integer::parseInt).sum();
    // 成绩统计信息（最高分、最低分、平均分）
    sheetMap.put("max", Collections.max(scoreList));
    sheetMap.put("min", Collections.min(scoreList));
    sheetMap.put("avg", sum / scores.size());
    Map<String, Object> map = averageGradeScore(paperId, gradeId);
    int[] scoresMap = (int[]) map.get("score");
    String[] mapKey = {"e", "d", "c", "b", "a"};
    for (int i = 0; i < scoresMap.length; i++) {
      sheetMap.put(mapKey[i] + "Num", scoresMap[i]);
      sheetMap.put(mapKey[i] + "Scale", (scoresMap[i] / (double) scores.size()) * 100);
    }
    // 及格率
    sheetMap.put("passScale", ((scores.size() - scoresMap[0]) / (double) scores.size()) * 100);
    //调用填充接口
    this.excelTemplateService.packingPaperAnalysis(sheetMap, response);
  }

  @Override
  public List<Score> selectByPaperIdAndGradeId(Integer paperId, Integer gradeId) {
    List<Score> scores = this.selectByPaperId(paperId);
    // 过滤出该班级的成绩集合
    scores = scores.stream().filter(
        score -> this.studentDAO.selectById(score.getStuId()).getGradeId().equals(gradeId))
        .collect(Collectors.toList());
    return scores;
  }

  /**
   * 统计各个分数段人数
   *
   * @param paperList 试卷及合
   * @param countMap  分数 Map 集合
   * @param countList 各个分数段 List 集合
   */
  private void countPerScoreNum(
      List<Paper> paperList, Map<String, Object> countMap, List<Map<String, Object>> countList) {

    // 预备用于计算各科成绩等级分布数量 E,D,C,B,A
    long eNum = 0L;
    long dNum = 0L;
    long cNum = 0L;
    long bNum = 0L;
    long aNum = 0L;

    // 循环该学生的分数 List 集合并计算出各门考试成绩分数分布信息
    if (CollUtil.isNotEmpty(paperList)) {
      for (Paper ignored : paperList) {
        // 不及格人数 E
        eNum += this.countLe("60");
        countMap.put("60分以下", eNum);
        // 60到70 D
        dNum += this.count("60", "70");
        countMap.put("60-70分", dNum);
        // 70到80 C
        cNum += this.count("70", "80");
        countMap.put("70-80分", cNum);
        // 80到90 B
        bNum += this.count("80", "90");
        countMap.put("80-90分", bNum);
        // 90到100 A
        aNum += this.countGt("90");
        countMap.put("90分以上", aNum);
      }
      countList.add(countMap);
    }
  }

  /**
   * 统计成绩大于改分数的数量
   *
   * @param score 成绩
   * @return 数量
   */
  private int countGt(String score) {
    // 使用 QueryWrapper 构造查询条件，下面的三个方法同理
    LambdaQueryWrapper<Score> qw = new LambdaQueryWrapper<>();
    // gt（GreatThan） 大于
    qw.gt(Score::getScore, score);
    return this.scoreDAO.selectCount(qw);
  }

  /**
   * 统计成绩小于改分数的数量
   *
   * @param score 成绩
   * @return 数量
   */
  private int countLe(String score) {
    LambdaQueryWrapper<Score> qw = new LambdaQueryWrapper<>();
    // le（lessThan）小于
    qw.le(Score::getScore, score);
    return this.scoreDAO.selectCount(qw);
  }

  /**
   * 统计成绩介于两者之间的数量
   *
   * @param start 开始分数
   * @param end   结束分数
   * @return 数量
   */
  private int count(String start, String end) {
    LambdaQueryWrapper<Score> qw = new LambdaQueryWrapper<>();
    // between 介于两者之间
    qw.between(Score::getScore, start, end);
    return this.scoreDAO.selectCount(qw);
  }
}
