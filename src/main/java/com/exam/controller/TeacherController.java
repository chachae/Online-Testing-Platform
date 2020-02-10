package com.exam.controller;

import com.exam.common.Page;
import com.exam.common.R;
import com.exam.constant.SysConsts;
import com.exam.entity.*;
import com.exam.entity.dto.AnswerEditDto;
import com.exam.entity.dto.ChangePassDto;
import com.exam.entity.dto.StudentAnswerDto;
import com.exam.entity.dto.StudentQueryDto;
import com.exam.entity.vo.MajorVo;
import com.exam.entity.vo.StudentVo;
import com.exam.exception.ServiceException;
import com.exam.service.*;
import com.exam.util.HttpContextUtil;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 教师模块控制层
 *
 * @author yzn
 * @date 2020/2/5
 */
@Controller
@RequestMapping("/teacher")
public class TeacherController {

  @Resource private TeacherService teacherService;
  @Resource private PaperService paperService;
  @Resource private QuestionService questionService;
  @Resource private StudentService studentService;
  @Resource private StuAnswerRecordService stuAnswerRecordService;
  @Resource private ScoreService scoreService;
  @Resource private MajorService majorService;
  @Resource private AcademyService academyService;
  /**
   * 教师登录
   *
   * @param teacherId 教师ID
   * @param teacherPassword 教师密码
   * @param r 重定向对象
   * @return 教师主页
   */
  @PostMapping("/login")
  public String login(String teacherId, String teacherPassword, RedirectAttributes r) {
    // 获取 session 对象
    HttpSession session = HttpContextUtil.getSession();
    try {
      // 执行登录
      Teacher teacher = teacherService.login(teacherId, teacherPassword);
      // 设置 session 信息
      session.setAttribute(SysConsts.SESSION.TEACHER_ID, teacher.getId());
      session.setAttribute(SysConsts.SESSION.TEACHER, teacher);
      // 重定向到教师主页
      return "redirect:/teacher/home/" + teacher.getId();
    } catch (Exception e) {
      // 捕捉账号密码异常信息
      r.addFlashAttribute("message_tea", e.getMessage());
      return "redirect:/";
    }
  }

  /**
   * 教师修改密码页面
   *
   * @param id 教师ID
   * @return 页面
   */
  @GetMapping("/{id}/changePass")
  public String changePass(@PathVariable Integer id) {
    return "teacher/changePass";
  }

  /**
   * 修改密码
   *
   * @param id 教师ID
   * @param dto 密码信息
   * @return 重定向登录页面
   */
  @PostMapping("/{id}/changePass")
  public String changePass(@PathVariable Integer id, ChangePassDto dto, RedirectAttributes r) {
    // 获取 session 对象
    HttpSession session = HttpContextUtil.getSession();
    // 调用密码修改接口
    try {
      teacherService.changePassword(id, dto);
    } catch (ServiceException e) {
      r.addFlashAttribute("message", e.getMessage());
      return "redirect:/teacher/" + id + "/changePass";
    }
    // 移除 session 信息
    session.removeAttribute(SysConsts.SESSION.TEACHER_ID);
    session.removeAttribute(SysConsts.SESSION.TEACHER);
    // 重定向登录页面
    return "redirect:/";
  }

  /**
   * 教师个人主页
   *
   * @param model model 对象
   * @param id 教师ID
   * @return 教师主页
   */
  @GetMapping("/home/{id}")
  public String home(Model model, @PathVariable Integer id) {
    // 调用通过 ID 查询教师信息接口
    Teacher teacher = teacherService.getById(id);
    model.addAttribute("teacher", teacher);
    return "teacher/home";
  }

  /**
   * 教师账号退出
   *
   * @return 重定向到登录页面
   */
  @GetMapping("/logout")
  public String logout() {
    HttpSession session = HttpContextUtil.getSession();
    // 移除session
    session.removeAttribute(SysConsts.SESSION.TEACHER_ID);
    session.removeAttribute(SysConsts.SESSION.TEACHER);
    // 重定向到登录页面
    return "redirect:/";
  }

  /**
   * 展示该教师所教课程
   *
   * @param model model 对象
   * @return 课程信息
   */
  @GetMapping("/course/list")
  public String courseList(Model model) {
    HttpSession session = HttpContextUtil.getSession();
    // 从 session 中获取教师ID
    Integer teacherId = (Integer) session.getAttribute(SysConsts.SESSION.TEACHER_ID);
    // 获取当前教师的所有课程信息
    List<Course> courseList = teacherService.findAllCourseByTeacherId(teacherId);
    // 设置 model 信息
    model.addAttribute("courseList", courseList);
    return "teacher/courseList";
  }

  /**
   * 添加课程
   *
   * @param courseName 课程名称
   * @param teacherId 教师ID
   * @return 成功信息
   */
  @GetMapping("/course/new")
  @ResponseBody
  public R newCourse(String courseName, Integer teacherId) {
    // 调用保存接口
    teacherService.saveCourse(courseName, teacherId);
    return R.success();
  }

  /**
   * 删除课程
   *
   * @param id 课程ID
   * @return 删除成功信息
   */
  @GetMapping("/course/del/{id}")
  @ResponseBody
  public R delCourse(@PathVariable Integer id) {
    try {
      // 调用课程删除接口
      teacherService.delCourseById(id);
      return R.success();
    } catch (Exception e) {
      return R.error(e.getMessage());
    }
  }

  /**
   * 查看系统公告列表
   *
   * @param model model 对象
   * @return 公告集合
   */
  @GetMapping("/announce/system")
  public String announceSystem(Model model) {
    // 调用公告查询接口
    List<Announce> announceList = teacherService.findAllSystemAnnounce();
    // 设置 model 对象信息
    model.addAttribute("announceList", announceList);
    return "teacher/sysAnnounceList";
  }

  /**
   * 考试管理 可以修改考试时间
   *
   * @param model model 对象
   * @return 考试信息
   */
  @GetMapping("/exam")
  public String exam(Model model) {
    // 获取教师 ID
    HttpSession session = HttpContextUtil.getSession();
    Integer id = (Integer) session.getAttribute(SysConsts.SESSION.TEACHER_ID);
    // 查询符合条件的时间 List 集合
    List<Paper> paperList = paperService.findUnDoPaperListByTeacherId(id);
    // 设置 model 对象信息
    model.addAttribute("paperList", paperList);
    return "teacher/examList";
  }

  /**
   * 编辑考试信息
   *
   * @param id 试卷ID
   * @param paper 试卷信息
   * @return 成功信息
   */
  @PostMapping("/editPaper/{id}")
  @ResponseBody
  public R editExam(@PathVariable Integer id, Paper paper) {
    paperService.editPaperById(id, paper);
    return R.success();
  }

  /**
   * 试卷复查
   *
   * @param model model 对象
   * @return 试卷集合
   */
  @GetMapping("/reviewPaper")
  public String reviewPaper(Model model) {
    // 获取 session 对象
    HttpSession session = HttpContextUtil.getSession();
    // 获取盖老师所属的已完成的试卷信息
    Integer teacherId = (Integer) session.getAttribute(SysConsts.SESSION.TEACHER_ID);
    List<Paper> paperList = paperService.findDonePaperListByTeacherId(teacherId);
    // 返回 Model 对象
    model.addAttribute("paperList", paperList);
    return "teacher/review";
  }

  /**
   * 复查某个同学的试卷
   *
   * @param stuNumber 学生学号
   * @param paperId 试卷ID
   * @param model model 对象
   * @return 待复查试卷信息
   */
  @GetMapping("/reviewRes")
  public String reviewPaper(String stuNumber, Integer paperId, Model model, RedirectAttributes r) {
    // 查询该学生得试卷答题记录
    List<StuAnswerRecord> answerRecords;
    List<Question> questionList;
    try {
      answerRecords = paperService.findAnswerRecordByStuAndPaper(stuNumber, paperId);
      // 根据答案记录集合查找正确答案集合
      questionList = questionService.findByAnswerRecordList(answerRecords);
    } catch (ServiceException e) {
      r.addFlashAttribute("message", e.getMessage());
      return "redirect:/teacher/reviewPaper";
    }
    // 将学生的题目和答案组装成 Map
    List<StudentAnswerDto> res =
        questionService.findMapByStuAnswerRecordAndQuestionList(answerRecords, questionList);
    // 通过学号查询该学生的信息
    StudentVo student = studentService.findVoByStuNumber(stuNumber);
    // 查询这张试卷的信息
    Paper paper = paperService.getById(paperId);
    // 查询分数情况
    Score score = this.scoreService.findByStuIdAndPaperId(student.getId(), paperId);
    // 设置 Model 对象信息
    model.addAttribute("answerRecords", answerRecords);
    model.addAttribute("questionList", questionList);
    model.addAttribute("stuAnswer", res);
    model.addAttribute("student", student);
    model.addAttribute("paper", paper);
    model.addAttribute("score", score);
    return "teacher/answerRecord";
  }

  /**
   * 修改主观题成绩
   *
   * @param dto 信息
   */
  @ResponseBody
  @PostMapping("/editScore/{id}")
  public R editScore(@PathVariable Integer id, AnswerEditDto dto) {
    // 修改该题得分
    StuAnswerRecord record = new StuAnswerRecord();
    record.setId(dto.getId()).setScore(dto.getNewScore());
    this.stuAnswerRecordService.updateById(record);
    // 修改总分
    this.scoreService.updateScoreByStuIdAndPaperId(dto);
    return R.success();
  }

  /**
   * 教师帮助页面
   *
   * @return 教师帮助页面
   */
  @GetMapping("/help")
  public String help() {
    return "teacher/help";
  }

  /**
   * 修改试卷状态：未开始 to 已结束
   *
   * @param id 试卷ID
   * @return 成功信息
   */
  @GetMapping("/changePaperState/{id}")
  @ResponseBody
  public R changPaperState(@PathVariable Integer id) {
    paperService.changeStateById(id);
    return R.success();
  }

  /**
   * 学生分页查询
   *
   * @param page 分页数据
   * @param model model 对象
   * @return 分页结果集
   */
  @GetMapping("/student")
  public String listStudent(Page page, Model model, StudentQueryDto dto) {
    // 获取学生分页数据
    PageInfo<StudentVo> pageInfo = studentService.pageForStudentList(page.getPageNo(), dto);
    // 获取专业数据
    List<Major> majorList = this.majorService.list();
    // 获取学院数据
    List<Academy> academyList = this.academyService.list();
    // 设置分页后的数据的 model 对象
    model.addAttribute("page", pageInfo);
    model.addAttribute("majorList", majorList);
    model.addAttribute("academyList", academyList);
    return "teacher/studentList";
  }

  /**
   * 更新学生
   *
   * @param student 学生信息
   * @return 成功信息
   */
  @ResponseBody
  @PostMapping("/student/update")
  public R updateStudent(Student student) {
    this.studentService.updateById(student);
    return R.success();
  }

  /**
   * 增加学生
   *
   * @param student 学生信息
   * @return 成功信息
   */
  @ResponseBody
  @PostMapping("/student/save")
  public R saveStudent(Student student) {
    this.studentService.save(student);
    return R.success();
  }

  /**
   * 专业管理页面
   *
   * @return 专业管理页面
   */
  @GetMapping("/major")
  public String listMajor(Page page, Model model) {
    // 获取分页专业数据
    PageInfo<MajorVo> pageInfo = majorService.pageForMajorList(page.getPageNo());
    List<Academy> academies = this.academyService.list();
    // 设置数据 model 对象
    model.addAttribute("page", pageInfo);
    model.addAttribute("academyList", academies);
    return "teacher/majorList";
  }

  /**
   * 更新专业信息
   *
   * @param major 专业信息
   * @return 成功信息
   */
  @ResponseBody
  @PostMapping("/major/update")
  public R updateMajor(Major major) {
    this.majorService.updateById(major);
    return R.success();
  }

  /**
   * 新增专业信息
   *
   * @param major 专业信息
   * @return 成功信息
   */
  @ResponseBody
  @PostMapping("/major/save")
  public R saveMajor(Major major) {
    this.majorService.save(major);
    return R.success();
  }
}
