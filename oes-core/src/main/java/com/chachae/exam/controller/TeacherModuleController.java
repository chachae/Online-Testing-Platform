package com.chachae.exam.controller;

import com.chachae.exam.common.constant.SysConsts;
import com.chachae.exam.common.model.Paper;
import com.chachae.exam.common.model.dto.StuAnswerRecordDto;
import com.chachae.exam.common.util.HttpUtil;
import com.chachae.exam.common.util.ServletUtil;
import com.chachae.exam.service.CourseService;
import com.chachae.exam.service.MajorService;
import com.chachae.exam.service.PaperFormService;
import com.chachae.exam.service.PaperService;
import com.chachae.exam.service.QuestionService;
import com.chachae.exam.service.QuestionSortService;
import com.chachae.exam.service.StuAnswerRecordService;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * 教师模块控制层
 *
 * @author chachae
 * @date 2020/2/5
 */
@Controller
@RequestMapping("/teacher")
public class TeacherModuleController {

  @Resource private PaperService paperService;
  @Resource private MajorService majorService;
  @Resource private QuestionSortService questionSortService;
  @Resource private CourseService courseService;
  @Resource private QuestionService questionService;
  @Resource private PaperFormService paperFormService;
  @Resource private StuAnswerRecordService stuAnswerRecordService;

  /**
   * 教师账号退出
   *
   * @return 重定向到登录页面
   */
  @GetMapping("/logout")
  public String logout() {
    HttpSession session = HttpUtil.getSession();
    // 移除session
    session.removeAttribute(SysConsts.Session.TEACHER_ID);
    session.removeAttribute(SysConsts.Session.TEACHER);
    // 重定向到登录页面
    return "redirect:/";
  }

  /**
   * 展示该教师所教课程
   *
   * @return 课程信息
   */
  @GetMapping("/course")
  public String courseList() {
    return ServletUtil.isAjax() ? "/teacher/course/list#courseTable" : "/teacher/course/list";
  }

  /**
   * 教师修改密码页面
   *
   * @return 页面
   */
  @GetMapping("/update/pass")
  public String updatePass() {
    return ServletUtil.isAjax()
        ? "/teacher/self/update-pass#updateTable"
        : "/teacher/self/update-pass";
  }

  /**
   * 教师个人主页
   *
   * @return 教师主页
   */
  @GetMapping("/index")
  public String index() {
    return "/teacher/main/index";
  }

  /**
   * 教师个人主页
   *
   * @return 教师主页
   */
  @GetMapping("/home")
  public String home() {
    return ServletUtil.isAjax() ? "/teacher/self/home#homeTable" : "/teacher/self/home";
  }

  /**
   * 查看系统公告列表
   *
   * @return 公告集合
   */
  @GetMapping("/announce")
  public String announceSystem() {
    return ServletUtil.isAjax() ? "/teacher/announce/list#announceTable" : "/teacher/announce/list";
  }

  /**
   * 考试管理 可以修改考试时间
   *
   * @return 考试信息
   */
  @GetMapping("/exam")
  public String exam() {
    return ServletUtil.isAjax() ? "/teacher/exam/list#examTable" : "/teacher/exam/list";
  }

  /**
   * 试卷复查
   *
   * @param mv ModelAndView 对象
   * @return 试卷集合
   */
  @GetMapping("/reviewPaper")
  public ModelAndView reviewPaper(ModelAndView mv) {
    // 获取教师id
    int id = (int) HttpUtil.getAttribute(SysConsts.Session.TEACHER_ID);
    // 返回 Model 对象
    mv.addObject("paperList", paperService.listDoneByTeacherId(id));
    if (ServletUtil.isAjax()) {
      mv.setViewName("/teacher/review/list#reviewTable");
    } else {
      mv.setViewName("/teacher/review/list");
    }
    return mv;
  }

  /**
   * 复查某场考试的试卷
   *
   * @param paperId 试卷ID
   * @param mv ModelAndView 对象
   * @return 待复查试卷信息
   */
  @GetMapping("/reviewRes")
  public ModelAndView reviewPaper(Integer paperId, ModelAndView mv) {
    // 答题记录传输对象 List 集合
    List<StuAnswerRecordDto> records = this.stuAnswerRecordService.listStuAnswerRecordDto(paperId);
    mv.addObject("stuAnswer", records);
    mv.addObject("paper", this.paperService.getById(paperId));
    mv.addObject("questionList", this.questionService.listByStuAnswerRecordDto(records.get(0)));
    if (ServletUtil.isAjax()) {
      mv.setViewName("/teacher/review/record-list#reviewListTable");
    } else {
      mv.setViewName("/teacher/review/record-list");
    }
    return mv;
  }

  /**
   * 教师帮助页面
   *
   * @return 教师帮助页面
   */
  @GetMapping("/help")
  public String help() {
    return ServletUtil.isAjax() ? "/teacher/self/help#helpTable" : "/teacher/self/help";
  }

  /**
   * 试卷分页查询
   *
   * @return 分页结果集
   */
  @GetMapping("/paper")
  public String pagePaper() {
    return ServletUtil.isAjax() ? "/teacher/paper/list#paperTable" : "/teacher/paper/list";
  }

  /**
   * 查询试卷的详细信息
   *
   * @param id 试卷的ID
   * @param mv ModelAndView 对象
   * @return 此 ID 的试卷详细信息
   */
  @GetMapping("/paper/show/{id}")
  public ModelAndView show(@PathVariable Integer id, ModelAndView mv) {
    // 根据 ID 获取试卷的详细信息
    Paper paper = paperService.getById(id);
    // 设置基础 model 信息
    mv.addObject("paper", paper);
    mv.addObject("course", courseService.getById(paper.getCourseId()));
    mv.addObject("major", majorService.getById(paper.getMajorId()));
    // 设置题目 model 对象信息
    questionSortService.setQuestionModel(mv, id, false);
    if (ServletUtil.isAjax()) {
      mv.setViewName("/teacher/paper/show#paperShowTable");
    } else {
      mv.setViewName("/teacher/paper/show");
    }
    return mv;
  }

  /**
   * 转发到随机新增试卷模板页面
   *
   * @return 新的试卷模板页面
   */
  @GetMapping("/paperForm/save")
  public String savePaperForm() {
    return ServletUtil.isAjax()
        ? "/teacher/paper/save-paper-form#paperFormTable"
        : "/teacher/paper/save-paper-form";
  }

  /**
   * 导入试卷页面
   *
   * @return 导入试卷页面
   */
  @GetMapping("/paper/import")
  public String importNewPaper() {
    return ServletUtil.isAjax()
        ? "/teacher/paper/import-paper#importTable"
        : "/teacher/paper/import-paper";
  }

  /**
   * 添加试卷（组卷页面）
   *
   * @param id 试卷模板 ID
   * @param mv ModelAndView 对象
   * @return 组卷页面
   */
  @GetMapping("/paper/save/{id}")
  public ModelAndView add(@PathVariable Integer id, ModelAndView mv) {
    // 封装 model 参数
    mv.addObject("paperFormId", id);
    if (ServletUtil.isAjax()) {
      mv.setViewName("/teacher/paper/save-paper#savePaperTable");
    } else {
      mv.setViewName("/teacher/paper/save-paper");
    }
    return mv;
  }

  /**
   * 展示所有试卷模版列表
   *
   * @param mv ModelAndView 对象
   * @return 试卷模板页面
   */
  @GetMapping("/paperForm")
  public ModelAndView showPaperForm(ModelAndView mv) {
    // 调用获取试卷模板集合接口
    mv.addObject("formList", this.paperFormService.list());
    if (ServletUtil.isAjax()) {
      mv.setViewName("/teacher/paper/paper-form-list#paperFormTable");
    } else {
      mv.setViewName("/teacher/paper/paper-form-list");
    }
    return mv;
  }

  /**
   * 试题 List 集合
   *
   * @return 试题集合页面
   */
  @GetMapping("/question")
  public String list() {
    return ServletUtil.isAjax() ? "/teacher/question/list#questionTable" : "/teacher/question/list";
  }
}
