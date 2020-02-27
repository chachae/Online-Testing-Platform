package com.exam.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.exam.common.R;
import com.exam.constant.SysConsts;
import com.exam.controller.common.QuestionModel;
import com.exam.entity.Paper;
import com.exam.entity.Score;
import com.exam.entity.Student;
import com.exam.entity.dto.ChangePassDto;
import com.exam.exception.ServiceException;
import com.exam.service.*;
import com.exam.util.HttpContextUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 學生控制层
 *
 * @author yzn
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
   * 学生登录 验证学号和密码
   *
   * @param studentId 学号(stuNumber)
   * @param studentPassword 密码
   * @return 主界面
   */
  @ResponseBody
  @PostMapping("/login")
  public R login(String studentId, String studentPassword) {
    // 获取 session 对象
    HttpSession session = HttpContextUtil.getSession();
    try {
      // 执行登录接口
      Student student = studentService.login(studentId, studentPassword);
      // 设置 Session 信息
      session.setAttribute(SysConsts.SESSION.STUDENT_ID, student.getId());
      session.setAttribute(SysConsts.SESSION.STUDENT, student);
      // 重定向到学生主界面
      return R.successWithData(student.getId());
    } catch (ServiceException e) {
      return R.error(e.getMessage());
    }
  }

  /**
   * 学生登录后来到home页
   *
   * @param id 学生ID
   * @param model model 对象
   * @return 学生主界面
   */
  @GetMapping("/home/{id}")
  public String home(@PathVariable Integer id, Model model) {
    // 设置学生信息 model 对象信息
    model.addAttribute("student", studentService.getById(id));
    return "/student/home";
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
   * 密码修改
   *
   * @param id 学生ID
   * @param dto 密码信息
   * @return 重定向到登录界面
   */
  @ResponseBody
  @PostMapping("/{id}/changePass")
  public R changePass(@PathVariable Integer id, ChangePassDto dto) {
    // 通过获取 Session 对象
    HttpSession session = HttpContextUtil.getSession();
    try {
      // 调用密码修改接口
      this.studentService.updatePassword(id, dto);
      // 移除学生 session 信息
      session.removeAttribute(SysConsts.SESSION.STUDENT_ID);
      session.removeAttribute(SysConsts.SESSION.STUDENT);
      return R.success();
    } catch (ServiceException e) {
      // 捕捉密码修改失败异常
      return R.error(e.getMessage());
    }
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
   * @param model model 对象
   * @return 公告集合
   */
  @GetMapping("/announce/system")
  public String announceSystem(Model model) {
    // 设置公告集合的 model 对象信息
    model.addAttribute("announceList", this.announceService.list());
    return "/student/sysAnnounceList";
  }

  /**
   * 学生进入我的考试列表
   *
   * @param model model 对象
   * @return 学生考试信息页面
   */
  @GetMapping("/exam")
  public String exam(Model model) {
    // 获取学生的ID
    int id = (int) HttpContextUtil.getAttribute(SysConsts.SESSION.STUDENT_ID);
    Student student = studentService.getById(id);

    // 查询所有该专业的正式试卷
    List<Paper> paperList = paperService.selectByMajorId(student.getMajorId());
    model.addAttribute("paperList", paperList);

    // 模拟试卷 model 对象信息
    model.addAttribute(
        "practicePaperList", paperService.selectPracticePapersByMajorId(student.getMajorId()));
    return "/student/examList";
  }

  /**
   * 学生进入考试
   *
   * @param stuId 学生ID
   * @param paperId 试卷ID
   * @param model model 对象
   * @return 考试页面
   */
  @GetMapping("/{stuId}/paper/{paperId}")
  public String doPaper(@PathVariable Integer stuId, @PathVariable Integer paperId, Model model) {
    // 设置试卷信息的 model 对象信息
    model.addAttribute("paper", paperService.getById(paperId));
    // 各类题型的 model 对象信息
    questionModel.setQuestionModel(model, paperId);
    // 返回试卷
    return "student/paperDetail";
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
   * @param model model 对象
   * @return 成绩列表
   */
  @GetMapping("/score/{id}")
  public String scoreList(@PathVariable Integer id, Model model) {
    // 通过学生 ID 查询成绩集合并设置其 model 对象信息
    model.addAttribute("scoreList", this.scoreService.selectByStuId(id));
    return "student/scoreList";
  }

  /**
   * 学生帮助中心
   *
   * @return 学生帮助中心页面
   */
  @GetMapping("/help")
  public String help() {
    return "student/help";
  }

  /**
   * 试卷详情
   *
   * @param id 分数id
   * @param model model 对象
   * @return 详情
   */
  @GetMapping("/score/detail/{id}")
  public String scoreDetail(@PathVariable Integer id, Model model) {
    // id = score表对应id，设置 model 对象信息
    Score score = this.scoreService.getById(id);
    // 设置试卷信息
    Integer paperId = score.getPaperId();

    // 设置题目的 model 对象信息
    questionModel.setQuestionModel(model, paperId);

    // 设置分数和试卷 model 信息
    Paper paper = this.paperService.getById(paperId);
    model.addAttribute("score", score);
    model.addAttribute("paper", paper);

    // 设置课程和专业 model
    model.addAttribute("course", courseService.getById(paper.getCourseId()));
    model.addAttribute("major", majorService.getById(paper.getMajorId()));

    // 改造后的错题id
    model.addAttribute("wrongList", StrUtil.split(score.getWrongIds(), StrUtil.C_COMMA));
    return "/student/scoreDetail";
  }
}
