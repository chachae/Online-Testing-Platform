package com.chachae.exam.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.chachae.exam.common.constant.SysConsts;
import com.chachae.exam.common.model.Paper;
import com.chachae.exam.common.model.Score;
import com.chachae.exam.common.util.HttpUtil;
import com.chachae.exam.common.util.ServletUtil;
import com.chachae.exam.service.CourseService;
import com.chachae.exam.service.MajorService;
import com.chachae.exam.service.PaperService;
import com.chachae.exam.service.QuestionSortService;
import com.chachae.exam.service.ScoreService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * 學生控制层
 *
 * @author chachae
 * @date 2020/2/10
 */
@Controller
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentModuleController {

  private final QuestionSortService questionSortService;
  private final MajorService majorService;
  private final PaperService paperService;
  private final ScoreService scoreService;
  private final CourseService courseService;

  /**
   * 学生登录后来到home页
   *
   * @return 学生主界面
   */
  @GetMapping("/index")
  public String index() {
    return "/student/main/index";
  }

  /**
   * 学生登录后来到home页
   *
   * @return 学生主界面
   */
  @GetMapping("/home")
  public String home() {
    return ServletUtil.isAjax() ? "/student/self/home#homeTable" : "/student/self/home";
  }

  /**
   * 学生修改密码
   *
   * @return 密码修改界面
   */
  @GetMapping("/update/password")
  public String changePass() {
    return ServletUtil.isAjax()
        ? "/student/self/update-pass#updatePassTable"
        : "/student/self/update-pass";
  }

  /**
   * 查看系统公告列表
   *
   * @return 公告集合
   */
  @GetMapping("/announce")
  public String announceSystem() {
    return ServletUtil.isAjax() ? "/student/announce/list#announceTable" : "/student/announce/list";
  }

  /**
   * 学生进入我的考试列表
   *
   * @return 学生考试信息页面
   */
  @GetMapping("/exam")
  public String exam() {
    return ServletUtil.isAjax() ? "/student/exam/list#examTable" : "/student/exam/list";
  }

  /**
   * 查询成绩列表
   *
   * @return 成绩列表
   */
  @GetMapping("/score")
  public String scoreList() {
    return ServletUtil.isAjax() ? "/student/score/list#scoreTable" : "/student/score/list";
  }

  /**
   * 学生帮助中心
   *
   * @return 学生帮助中心页面
   */
  @GetMapping("/help")
  public String help() {
    return ServletUtil.isAjax() ? "/student/self/help#helpTable" : "/student/self/help";
  }

  /**
   * 转发学生分数雷达页面
   *
   * @return 学生分数雷达页面
   */
  @GetMapping("/score/chart")
  public String studentChart() {
    return ServletUtil.isAjax() ? "/student/chart/list#chartTable" : "/student/chart/list";
  }

  /**
   * 学生退出
   *
   * @return 登录界面
   */
  @GetMapping("/logout")
  public String logout() {
    // 通过 SpringContext 上下文获取 Session 对象
    HttpSession session = HttpUtil.getSession();
    // 移除学生 session 信息
    session.removeAttribute(SysConsts.Session.ROLE_ID);
    session.removeAttribute(SysConsts.Session.STUDENT_ID);
    session.removeAttribute(SysConsts.Session.STUDENT);
    return "redirect:/";
  }

  /**
   * 学生进入考试
   *
   * @param paperId 试卷ID
   * @return 考试页面
   */
  @GetMapping("/paper/{paperId}")
  public ModelAndView doPaper(@PathVariable Integer paperId, ModelAndView mv) {
    mv.setViewName("redirect:/student/exam");
    // 若试卷不存在，转发到"我的考试"界面
    Paper paper = paperService.getById(paperId);
    if (paper == null) {
      return mv;
    }
    // 设置试卷信息的 model 对象信息
    mv.addObject("paper", paper);
    // 各类题型的 model 对象信息
    questionSortService.setQuestionModel(mv, paperId, true);
    // 返回试卷
    if (ServletUtil.isAjax()) {
      mv.setViewName("/student/exam/detail#examDetailTable");
    } else {
      mv.setViewName("/student/exam/detail");
    }
    return mv;
  }

  /**
   * 学生进行考试 批改试卷
   *
   * @param paperId 试卷ID
   * @return 学生主页
   */
  @PostMapping("/paper/{paperId}")
  public String doPaper(@PathVariable Integer paperId) {
    // 判断学生是否提交过试卷
    int stuId = (int) HttpUtil.getAttribute(SysConsts.Session.STUDENT_ID);
    Score result = this.scoreService.selectByStuIdAndPaperId(stuId, paperId);
    // 集合为空说明没有提交过试卷，可以提交
    if (ObjectUtil.isEmpty(result)) {
      // 获取 request 对象
      HttpServletRequest request = HttpUtil.getHttpServletRequest();
      // 批改试卷
      this.paperService.markPaper(stuId, paperId, request);
    }
    // 批改完成，重定向到成绩列表页面
    return "redirect:/student/score";
  }

  /**
   * 试卷详情
   *
   * @param id 分数id
   * @param mv ModelAndView 对象
   * @return 详情
   */
  @GetMapping("/score/detail/{id}")
  public ModelAndView scoreDetail(@PathVariable Integer id, ModelAndView mv) {
    mv.setViewName("redirect:/student/score");
    // id = score表对应id，设置 model 对象信息
    Score score = this.scoreService.getById(id);
    if (score == null) {
      return mv;
    }
    // 设置试卷信息
    Integer paperId = score.getPaperId();

    // 设置题目的 model 对象信息
    questionSortService.setQuestionModel(mv, paperId, false);

    // 设置分数和试卷 model 信息
    Paper paper = this.paperService.getById(paperId);
    mv.addObject("score", score);
    mv.addObject("paper", paper);

    // 设置课程和专业 model
    mv.addObject("course", courseService.getById(paper.getCourseId()));
    mv.addObject("major", majorService.getById(paper.getMajorId()));

    // 改造后的错题id
    mv.addObject("wrongList", StrUtil.split(score.getWrongIds(), StrUtil.C_COMMA));
    if (ServletUtil.isAjax()) {
      mv.setViewName("/student/score/detail#scoreDetailTable");
    } else {
      mv.setViewName("/student/score/detail");
    }
    return mv;
  }
}
