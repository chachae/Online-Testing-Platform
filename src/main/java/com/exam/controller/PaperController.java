package com.exam.controller;

import cn.hutool.core.util.StrUtil;
import com.exam.common.Page;
import com.exam.common.R;
import com.exam.constant.SysConsts;
import com.exam.entity.*;
import com.exam.entity.dto.ImportPaperDto;
import com.exam.entity.dto.PaperQuestionUpdateDto;
import com.exam.exception.ServiceException;
import com.exam.service.*;
import com.exam.util.HttpContextUtil;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Set;

/**
 * 试卷控制层
 *
 * @author yzn
 * @date 2020/1/28
 */
@Controller
@RequestMapping("/teacher/paper")
public class PaperController {

  @Resource private PaperService paperService;
  @Resource private MajorService majorService;
  @Resource private CourseService courseService;
  @Resource private AcademyService academyService;
  @Resource private QuestionService questionService;
  @Resource private PaperFormService paperFormService;

  /**
   * 试卷分页查询
   *
   * @param page 分页数据
   * @param model model 对象
   * @return 分页结果集
   */
  @GetMapping
  public String list(Page page, Model model) {
    // 获取 session 对象
    HttpSession session = HttpContextUtil.getSession();
    Integer teacherId = (Integer) session.getAttribute(SysConsts.SESSION.TEACHER_ID);
    // 获取试卷分页数据
    PageInfo<Paper> pageInfo = paperService.pageForPaperList(teacherId, page.getPageNo());
    // 设置分页后的数据的 model 对象
    model.addAttribute("page", pageInfo);
    return "paper/list";
  }

  /**
   * 查询试卷的详细信息
   *
   * @param id 试卷的ID
   * @param model model 对象
   * @return 此 ID 的试卷详细信息
   */
  @GetMapping("/show/{id}")
  public String show(@PathVariable Integer id, Model model) {
    // 根据 ID 获取试卷的详细信息
    Paper paper = paperService.getById(id);
    // 根据课程 ID 获取课程信息
    Course course = courseService.getById(paper.getCourseId());
    // 根据专业 ID 获取专业信息
    Major major = majorService.getById(paper.getMajorId());
    // 设置 model 信息
    model.addAttribute("paper", paper);
    model.addAttribute("course", course);
    model.addAttribute("major", major);
    // 显示试卷信息
    Set<Question> qChoiceList =
            questionService.selectByPaperIdAndType(id, SysConsts.QUESTION.CHOICE_TYPE);
    Set<Question> qMulChoiceList =
            questionService.selectByPaperIdAndType(id, SysConsts.QUESTION.MUL_CHOICE_TYPE);
    Set<Question> qTofList =
            questionService.selectByPaperIdAndType(id, SysConsts.QUESTION.TOF_TYPE);
    Set<Question> qFillList =
            questionService.selectByPaperIdAndType(id, SysConsts.QUESTION.FILL_TYPE);
    Set<Question> qSaqList =
            questionService.selectByPaperIdAndType(id, SysConsts.QUESTION.SAQ_TYPE);
    Set<Question> qProgramList =
            questionService.selectByPaperIdAndType(id, SysConsts.QUESTION.PROGRAM_TYPE);
    // 设置 model 对象信息
    model.addAttribute("qChoiceList", qChoiceList);
    model.addAttribute("qMulChoiceList", qMulChoiceList);
    model.addAttribute("qTofList", qTofList);
    model.addAttribute("qFillList", qFillList);
    model.addAttribute("qSaqList", qSaqList);
    model.addAttribute("qProgramList", qProgramList);
    return "paper/show";
  }

  /**
   * 转发到随机新增试卷模板页面
   *
   * @return 新的试卷模板页面
   */
  @GetMapping("/newPaperForm")
  public String addPaperForm() {
    return "paper/newPaperForm";
  }

  @GetMapping("/importNewPaper")
  public String importNewPaper(Model model) {
    // 获取 session 对象
    HttpSession session = HttpContextUtil.getSession();
    // 获取教师 session ID
    Integer teacherId = (Integer) session.getAttribute(SysConsts.SESSION.TEACHER_ID);
    List<Course> courseList = this.courseService.listByTeacherId(teacherId);
    // 专业列表
    List<Major> majors = this.majorService.list();
    // 学院列表
    List<Academy> academyList = this.academyService.list();
    // 封装 model 参数
    model.addAttribute("academyList", academyList);
    model.addAttribute("courseList", courseList);
    model.addAttribute("majorList", majors);
    return "paper/importNewPaper";
  }

  @ResponseBody
  @PostMapping("/import/excel")
  public R excel(@RequestParam("file") MultipartFile multipartFile) {
    try {
      ImportPaperDto dto = this.questionService.importPaper(multipartFile);
      return R.successWithData(dto);
    } catch (Exception e) {
      return R.error(e.getMessage());
    }
  }

  @ResponseBody
  @PostMapping("/importNewPaper")
  public R newPaperByExcel(Paper paper) {
    try {
      HttpSession session = HttpContextUtil.getSession();
      // 获取教师 session ID
      Integer teacherId = (Integer) session.getAttribute(SysConsts.SESSION.TEACHER_ID);
      // 设置出卷老师
      paper.setTeacherId(teacherId);
      this.paperService.save(paper);
      return R.successWithData(paper.getId());
    } catch (Exception e) {
      return R.error(e.getMessage());
    }
  }

  /**
   * 试卷题型模版
   *
   * @param paperForm 试卷题型
   * @return 试卷题型页面
   */
  @PostMapping({"/newPaperForm"})
  public String addPaperForm(PaperForm paperForm) {
    // 调用试卷模板新增接口
    this.paperFormService.save(paperForm);
    return "redirect:/teacher/paper/newPaper/" + paperForm.getId();
  }

  /**
   * 添加试卷（组卷页面）
   *
   * @param model model 对象
   * @param id (paperForm的id，前端设置默认模版为1)
   * @return 组卷页面
   */
  @GetMapping("/newPaper/{id}")
  public String add(Model model, @PathVariable Integer id) {
    // 获取 session 对象
    HttpSession session = HttpContextUtil.getSession();
    // 获取教师 session ID
    Integer teacherId = (Integer) session.getAttribute(SysConsts.SESSION.TEACHER_ID);
    List<Course> courseList = this.courseService.listByTeacherId(teacherId);
    // 专业列表
    List<Major> majors = this.majorService.list();
    // 学院列表
    List<Academy> academyList = this.academyService.list();
    // 封装 model 参数
    model.addAttribute("academyList", academyList);
    model.addAttribute("courseList", courseList);
    model.addAttribute("majorList", majors);
    return "paper/newPaper";
  }

  /**
   * 提交组卷信息
   *
   * @param paper 试卷信息
   * @param paperFormId 试卷模板ID
   * @return 组卷页面
   */
  @ResponseBody
  @PostMapping("/newPaper/{paperFormId}")
  public R add(Paper paper, @PathVariable Integer paperFormId, String difficulty) {
    // 设置试卷模板 ID
    paper.setPaperFormId(paperFormId);
    try {
      // 获取 session 对象，获取家教师ID
      HttpSession session = HttpContextUtil.getSession();
      Integer teacherId = (Integer) session.getAttribute(SysConsts.SESSION.TEACHER_ID);
      // 调用组卷接口
      paper.setTeacherId(teacherId);
      // 判断是否指定难度
      if (StrUtil.isBlank(difficulty)) {
        paperService.randomNewPaper(paper);
      } else {
        // 带指定难度的接口
        paperService.randomNewPaper(paper, difficulty);
      }
      return R.successWithData(paper.getId());
    } catch (ServiceException e) {
      return R.error(e.getMessage());
    }
  }

  /**
   * 展示所有试卷模版列表
   *
   * @param model model 对象
   * @return 试卷模板页面
   */
  @GetMapping("/showPaperForm")
  public String showPaperForm(Model model) {
    // 调用获取试卷模板集合接口
    List<PaperForm> formList = this.paperFormService.list();
    model.addAttribute("formList", formList);
    return "paper/showPaperForm";
  }

  /**
   * 删除模版不跳转页面
   *
   * @param id 试卷模板ID
   * @return 当前试卷模板页面
   */
  @GetMapping("/delPaperForm/{id}")
  @ResponseBody
  public R delPaperForm(@PathVariable Integer id) {
    try {
      // 调用试卷模板已出接口（通过ID删除）
      this.paperFormService.removeById(id);
      return R.success();
    } catch (ServiceException e) {
      return R.error(e.getMessage());
    }
  }

  /**
   * 级联删除试卷、分数、答案记录
   *
   * @param id 试卷ID
   * @return 试卷页面
   */
  @GetMapping("/delete/{id}")
  public String delPaper(@PathVariable Integer id) {
    // 级联删除试卷（详见接口实现类）
    paperService.delPaperById(id);
    return "redirect:/teacher/paper";
  }

  /**
   * 修改試卷题目答案
   *
   * @param question 问题信息
   * @return 回调信息
   */
  @ResponseBody
  @PostMapping("/updateAnswer")
  public R updateAnswer(Question question) {
    this.questionService.updateById(question);
    return R.success();
  }

  /**
   * 修改試卷题目
   *
   * @param dto 修改的信息
   * @return 回调信息
   */
  @ResponseBody
  @PostMapping("/updateQuestion")
  public R updateQuestionId(PaperQuestionUpdateDto dto) {
    try {
      this.paperService.updateQuestionId(dto);
      return R.success();
    } catch (ServiceException e) {
      return R.error(e.getMessage());
    }
  }
}
