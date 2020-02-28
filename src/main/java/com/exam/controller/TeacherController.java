package com.exam.controller;

import com.exam.common.Page;
import com.exam.common.R;
import com.exam.constant.SysConsts;
import com.exam.entity.*;
import com.exam.entity.dto.AnswerEditDto;
import com.exam.entity.dto.ChangePassDto;
import com.exam.entity.dto.StuAnswerRecordDto;
import com.exam.entity.dto.StudentQueryDto;
import com.exam.exception.ServiceException;
import com.exam.service.*;
import com.exam.util.HttpContextUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
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

  @Resource private PaperService paperService;
  @Resource private ScoreService scoreService;
  @Resource private MajorService majorService;
  @Resource private CourseService courseService;
  @Resource private TeacherService teacherService;
  @Resource private AcademyService academyService;
  @Resource private StudentService studentService;
  @Resource private QuestionService questionService;
  @Resource private AnnounceService announceService;
  @Resource private StuAnswerRecordService stuAnswerRecordService;
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
    } catch (ServiceException e) {
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
      teacherService.updatePassword(id, dto);
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
   * @param mv ModelAndView 对象
   * @param id 教师ID
   * @return 教师主页
   */
  @GetMapping("/home/{id}")
  public ModelAndView home(ModelAndView mv, @PathVariable Integer id) {
    // 调用通过 ID 查询教师信息接口
    mv.addObject("teacher", teacherService.getById(id));
    mv.setViewName("/teacher/self/home");
    return mv;
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
   * @param mv ModelAndView 对象
   * @return 课程信息
   */
  @GetMapping("/course/list")
  public ModelAndView courseList(ModelAndView mv) {
    Object id = HttpContextUtil.getAttribute(SysConsts.SESSION.TEACHER_ID);
    // 设置 model 信息
    mv.addObject("courseList", courseService.listByTeacherId((int) id));
    mv.setViewName("/teacher/course/list");
    return mv;
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
    // 封装参数
    Course build = Course.builder().courseName(courseName).teacherId(teacherId).build();
    this.courseService.save(build);
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
      this.courseService.removeById(id);
      return R.success();
    } catch (ServiceException e) {
      return R.error(e.getMessage());
    }
  }

  /**
   * 查看系统公告列表
   *
   * @param mv ModelAndView 对象
   * @return 公告集合
   */
  @GetMapping("/announce/system")
  public ModelAndView announceSystem(ModelAndView mv) {
    // 设置 model 对象信息
    mv.addObject("announceList", this.announceService.list());
    mv.setViewName("/teacher/announce/list");
    return mv;
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
    Object id = HttpContextUtil.getAttribute(SysConsts.SESSION.TEACHER_ID);
    // 设置 model 对象信息
    model.addAttribute("paperList", paperService.listUnDoByTeacherId((int) id));
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
    try {
      this.paperService.updateById(id, paper);
      return R.success();
    } catch (ServiceException e) {
      return R.error(e.getMessage());
    }
  }

  /**
   * 试卷复查
   *
   * @param model model 对象
   * @return 试卷集合
   */
  @GetMapping("/reviewPaper")
  public String reviewPaper(Model model) {
    // 获取教师id
    Object id = HttpContextUtil.getAttribute(SysConsts.SESSION.TEACHER_ID);
    // 返回 Model 对象
    model.addAttribute("paperList", paperService.listDoneByTeacherId((int) id));
    return "teacher/review";
  }

  /**
   * 复查某场考试的试卷
   *
   * @param paperId 试卷ID
   * @param model model 对象
   * @return 待复查试卷信息
   */
  @GetMapping("/reviewRes")
  public String reviewPaper(Integer paperId, Model model, RedirectAttributes r) {
    try {
      // 答题记录传输对象 List 集合
      List<StuAnswerRecordDto> records =
          this.stuAnswerRecordService.listStuAnswerRecordDto(paperId);
      model.addAttribute("stuAnswer", records);
      model.addAttribute("paper", this.paperService.getById(paperId));
      model.addAttribute(
          "questionList", this.questionService.listByStuAnswerRecordDto(records.get(0)));
      return "teacher/answerRecord";
    } catch (ServiceException e) {
      r.addFlashAttribute("message", e.getMessage());
      return "redirect:/teacher/reviewPaper";
    }
  }

  /**
   * 修改主观题成绩
   *
   * @param dto 信息
   */
  @ResponseBody
  @PostMapping("/editScore")
  public R editScore(AnswerEditDto dto) {
    // 修改该题得分
    StuAnswerRecord record = new StuAnswerRecord();
    record.setId(dto.getId()).setScore(dto.getNewScore());
    this.stuAnswerRecordService.updateById(record);
    // 修改总分
    StuAnswerRecord stuRec = this.stuAnswerRecordService.getById(dto.getId());
    // 封装参数
    dto.setStuId(stuRec.getStuId());
    dto.setPaperId(stuRec.getPaperId());
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
    this.paperService.updateStateById(id);
    return R.success();
  }

  /**
   * 学生分页查询
   *
   * @param page 分页数据
   * @param mv ModelAndView 对象
   * @return 分页结果集
   */
  @GetMapping("/student")
  public ModelAndView listStudent(Page page, ModelAndView mv, StudentQueryDto dto) {

    // 设置分页后的数据的 model 对象
    mv.addObject("page", this.studentService.pageForStudentList(page.getPageNo(), dto));
    mv.addObject("majorList", this.majorService.list());

    // 当前查询的学院 ID
    if (dto.getAcademyId() != null) {
      mv.addObject("curAcademyId", dto.getAcademyId());
    } else {
      mv.addObject("curAcademyId", null);
    }

    // 当前查询的学院 ID
    if (dto.getName() != null) {
      mv.addObject("curName", dto.getName());
    } else {
      mv.addObject("curName", null);
    }
    mv.setViewName("/teacher/student/list");
    return mv;
  }

  /**
   * 专业管理页面
   *
   * @return 专业管理页面
   */
  @GetMapping("/major")
  public ModelAndView listMajor(Page page, ModelAndView mv, Major major) {
    // 设置数据 model 对象
    mv.addObject("page", this.majorService.pageForMajorList(page.getPageNo(), major));
    mv.addObject("academyList", this.academyService.list());
    // 设置当前选中的学院id
    if (major.getAcademyId() != null) {
      mv.addObject("curAcademyId", major.getAcademyId());
    }
    mv.setViewName("teacher/major/list");
    return mv;
  }
}
