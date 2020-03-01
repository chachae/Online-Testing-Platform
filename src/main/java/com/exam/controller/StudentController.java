package com.exam.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.exam.common.Page;
import com.exam.constant.SysConsts;
import com.exam.controller.common.QuestionModel;
import com.exam.entity.Paper;
import com.exam.entity.Score;
import com.exam.entity.Student;
import com.exam.service.*;
import com.exam.util.HttpContextUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 學生控制层
 *
 * @author chachae
 * @date 2020/2/10
 */
@Controller
@RequestMapping("/student")
public class StudentController {

  @Resource private QuestionModel questionModel;
  @Resource private MajorService majorService;
  @Resource private PaperService paperService;
  @Resource private ScoreService scoreService;
  @Resource private CourseService courseService;
  @Resource private StudentService studentService;
  @Resource private AnnounceService announceService;

  /**
   * 学生登录后来到home页
   *
   * @param id 学生ID
   * @param mv ModelAndView 对象
   * @return 学生主界面
   */
  @GetMapping("/home/{id}")
  public ModelAndView home(@PathVariable Integer id, ModelAndView mv) {
    // 设置学生信息 model 对象信息
    mv.addObject("student", this.studentService.getById(id));
    mv.setViewName("/student/self/home");
    return mv;
  }

  /**
   * 学生修改密码
   *
   * @param id 学生ID
   * @return 密码修改界面
   */
  @GetMapping("/{id}/changePass")
  public String changePass(@PathVariable Integer id) {
    return "/student/changePass";
  }

  /**
   * 学生退出
   *
   * @return 登录界面
   */
  @GetMapping("/logout")
  public String logout() {
    // 通过 SpringContext 上下文获取 Session 对象
    HttpSession session = HttpContextUtil.getSession();
    // 移除学生 session 信息
    session.removeAttribute(SysConsts.SESSION.STUDENT_ID);
    session.removeAttribute(SysConsts.SESSION.STUDENT);
    return "redirect:/";
  }

  /**
   * 查看系统公告列表
   *
   * @param mv ModelAndView 对象
   * @return 公告集合
   */
  @GetMapping("/announce")
  public ModelAndView announceSystem(ModelAndView mv) {
    // 设置公告集合的 model 对象信息
    mv.addObject("announceList", this.announceService.list());
    mv.setViewName("/student/announce/list");
    return mv;
  }

  /**
   * 学生进入我的考试列表
   *
   * @param mv ModelAndView 对象
   * @return 学生考试信息页面
   */
  @GetMapping("/exam")
  public ModelAndView exam(ModelAndView mv) {
    // 获取学生的ID
    int id = (int) HttpContextUtil.getAttribute(SysConsts.SESSION.STUDENT_ID);
    Student student = studentService.getById(id);

    // 查询所有该专业的正式试卷
    List<Paper> papers = paperService.selectByMajorId(student.getMajorId());
    mv.addObject("paperList", papers);

    // 模拟试卷 model 对象信息
    List<Paper> pracitces = paperService.selectPracticePapersByMajorId(student.getMajorId());
    mv.addObject("practicePaperList", pracitces);
    mv.setViewName("/student/exam/list");
    return mv;
  }

  /**
   * 学生进入考试
   *
   * @param paperId 试卷ID
   * @return 考试页面
   */
  @GetMapping("/{stuId}/paper/{paperId}")
  public ModelAndView doPaper(@PathVariable Integer paperId, ModelAndView mv) {
    // 设置试卷信息的 model 对象信息
    mv.addObject("paper", paperService.getById(paperId));
    // 各类题型的 model 对象信息
    questionModel.setQuestionModel(mv, paperId);
    // 返回试卷
    mv.setViewName("/student/exam/detail");
    return mv;
  }

  /**
   * 学生进行考试 批改试卷
   *
   * @param stuId 学生ID
   * @param paperId 试卷ID
   * @return 学生主页
   */
  @PostMapping("/{stuId}/paper/{paperId}")
  public String doPaper(@PathVariable Integer stuId, @PathVariable Integer paperId) {
    // 判断学生是否提交过试卷
    Score result = this.scoreService.selectByStuIdAndPaperId(stuId, paperId);
    // 集合为空说明没有提交过试卷，可以提交
    if (ObjectUtil.isEmpty(result)) {
      // 获取 request 对象
      HttpServletRequest request = HttpContextUtil.getHttpServletRequest();
      // 批改试卷
      this.paperService.markPaper(stuId, paperId, request);
    }
    return "redirect:/student/home/" + stuId;
  }

  /**
   * 查询成绩列表
   *
   * @param id 学生ID
   * @param mv ModelAndView 对象
   * @return 成绩列表
   */
  @GetMapping("/score/{id}")
  public ModelAndView scoreList(@PathVariable Integer id, ModelAndView mv) {
    // 通过学生 ID 查询成绩集合并设置其 model 对象信息
    mv.addObject("scoreList", this.scoreService.selectByStuId(id));
    mv.setViewName("/student/score/list");
    return mv;
  }

  /**
   * 学生帮助中心
   *
   * @return 学生帮助中心页面
   */
  @GetMapping("/help")
  public String help() {
    return "/student/self/help";
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
    // id = score表对应id，设置 model 对象信息
    Score score = this.scoreService.getById(id);
    // 设置试卷信息
    Integer paperId = score.getPaperId();

    // 设置题目的 model 对象信息
    questionModel.setQuestionModel(mv, paperId);

    // 设置分数和试卷 model 信息
    Paper paper = this.paperService.getById(paperId);
    mv.addObject("score", score);
    mv.addObject("paper", paper);

    // 设置课程和专业 model
    mv.addObject("course", courseService.getById(paper.getCourseId()));
    mv.addObject("major", majorService.getById(paper.getMajorId()));

    // 改造后的错题id
    mv.addObject("wrongList", StrUtil.split(score.getWrongIds(), StrUtil.C_COMMA));
    mv.setViewName("/student/score/detail");
    return mv;
  }

  /**
   * 试卷分页查询
   *
   * @param page 分页数据
   * @param mv ModelAndView 对象
   * @return 分页结果集
   */
  @GetMapping("/paper")
  public ModelAndView list(Page page, ModelAndView mv) {
    // 获取教师 ID
    int teacherId = (int) HttpContextUtil.getAttribute(SysConsts.SESSION.TEACHER_ID);
    // 设置分页后的数据的 model 对象
    mv.addObject("page", paperService.pageForPaperList(teacherId, page.getPageNo()));
    mv.setViewName("/teacher/paper/list");
    return mv;
  }

  /**
   * 转发学生分数雷达页面
   *
   * @return 学生分数雷达页面
   */
  @GetMapping("/score/chart")
  public String studentChart() {
    return "/student/chart/list";
  }
}
