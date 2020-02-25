package com.exam.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exam.constant.SysConsts;
import com.exam.entity.Course;
import com.exam.entity.Paper;
import com.exam.entity.Score;
import com.exam.entity.dto.AnswerEditDto;
import com.exam.mapper.CourseMapper;
import com.exam.mapper.PaperMapper;
import com.exam.mapper.ScoreMapper;
import com.exam.service.ScoreService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 分数业务实现
 *
 * @author yzn
 * @date 2020/1/7
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class ScoreServiceImpl extends ServiceImpl<ScoreMapper, Score> implements ScoreService {

  @Resource private ScoreMapper scoreMapper;
  @Resource private PaperMapper paperMapper;
  @Resource private CourseMapper courseMapper;

  @Override
  public List<Score> selectByStuId(Integer id) {
    // QueryWrapper 条件构造器构造查询 Sql
    QueryWrapper<Score> qw = new QueryWrapper<>();
    qw.lambda().eq(Score::getStuId, id);
    // 返回该学生的分数集合
    return scoreMapper.selectList(qw);
  }

  @Override
  public List<Map<String, Object>> countByCourseId(Integer courseId) {
    // 构造条件询条件
    QueryWrapper<Paper> qw = new QueryWrapper<>();
    qw.lambda().eq(Paper::getCourseId, courseId);
    qw.lambda().eq(Paper::getPaperType, SysConsts.PAPER.PAPER_TYPE_FORMAL);
    // 准备一个 Map 用来存储课程成绩信息
    Map<String, Object> countMap = Maps.newHashMap();
    List<Map<String, Object>> countList = Lists.newArrayList();
    // 调用统计各个分数段人数方法
    countPerScoreNum(paperMapper.selectList(qw), countMap, countList);
    return countList;
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
      double avg = scoreMapper.avgScoreByPaperId(s.getPaperId());
      // 通过试卷 ID 查询试卷信息
      Paper paper = paperMapper.selectById(s.getPaperId());
      // 通过课程 ID 查询课程信息
      Course course = courseMapper.selectById(paper.getCourseId());
      // 存入平均分
      avgMap.put(course.getCourseName(), String.valueOf(avg));
    }
    // 统计我的每门课程成绩
    for (Score s : scoreList) {
      String scoreMy = s.getScore();
      // 通过 ID 查询试卷信息
      Paper paper = paperMapper.selectById(s.getPaperId());
      // 通过课程 ID 查询课程信息
      Course course = courseMapper.selectById(paper.getCourseId());
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
    QueryWrapper<Score> qw = new QueryWrapper<>();
    qw.lambda().eq(Score::getStuId, dto.getStuId()).eq(Score::getPaperId, dto.getPaperId());
    Score res = this.scoreMapper.selectOne(qw);
    // 更新成绩
    UpdateWrapper<Score> uw = new UpdateWrapper<>();
    uw.lambda().eq(Score::getStuId, dto.getStuId()).eq(Score::getPaperId, dto.getPaperId());
    // 新的总成绩
    int score = Integer.parseInt(res.getScore()) - dto.getOldScore() + dto.getNewScore();
    // 构造条件（学生ID+试卷ID）更新 SQL
    uw.lambda().set(Score::getScore, score);
    uw.lambda().eq(Score::getStuId, dto.getStuId()).eq(Score::getPaperId, dto.getPaperId());
    this.scoreMapper.update(null, uw);
  }

  @Override
  public Score selectByStuIdAndPaperId(Integer stuId, Integer paperId) {
    // 构造学生id和试卷id查询的查询条件
    QueryWrapper<Score> qw = new QueryWrapper<>();
    qw.lambda().eq(Score::getStuId, stuId).eq(Score::getPaperId, paperId);
    // 返回查询到的数据
    return this.scoreMapper.selectOne(qw);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deleteByStuId(Integer stuId) {
    QueryWrapper<Score> qw = new QueryWrapper<>();
    qw.lambda().eq(Score::getStuId, stuId);
    this.scoreMapper.delete(qw);
  }

  /**
   * 统计各个分数段人数
   *
   * @param paperList 试卷及合
   * @param countMap 分数 Map 集合
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
    QueryWrapper<Score> qw = new QueryWrapper<>();
    // gt（GreatThan） 大于
    qw.lambda().gt(Score::getScore, score);
    return this.scoreMapper.selectCount(qw);
  }

  /**
   * 统计成绩小于改分数的数量
   *
   * @param score 成绩
   * @return 数量
   */
  private int countLe(String score) {
    QueryWrapper<Score> qw = new QueryWrapper<>();
    // le（lessThan）小于
    qw.lambda().le(Score::getScore, score);
    return this.scoreMapper.selectCount(qw);
  }

  /**
   * 统计成绩介于两者之间的数量
   *
   * @param start 开始分数
   * @param end 结束分数
   * @return 数量
   */
  private int count(String start, String end) {
    QueryWrapper<Score> qw = new QueryWrapper<>();
    // between 介于两者之间
    qw.lambda().between(Score::getScore, start, end);
    return this.scoreMapper.selectCount(qw);
  }
}
