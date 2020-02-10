package com.exam.controller;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.log.Log;
import com.exam.common.Page;
import com.exam.common.R;
import com.exam.constant.SysConsts;
import com.exam.entity.*;
import com.exam.exception.ServiceException;
import com.exam.service.MajorService;
import com.exam.service.PaperService;
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
 * 试卷控制层
 *
 * @author yzn
 * @date 2020/1/28
 */
@Controller
@RequestMapping("/teacher/paper")
public class PaperController {

  /** 获取当前系统日志 */
  private Log log = Log.get();

  /** 注入业务Bean */
  @Resource private PaperService paperService;
  @Resource private MajorService majorService;

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
    Course course = paperService.findCourseById(paper.getCourseId());
    // 根据专业 ID 获取专业信息
    Major major = paperService.findMajorById(paper.getMajorId());
    // 设置 model 信息
    model.addAttribute("paper", paper);
    model.addAttribute("course", course);
    model.addAttribute("major", major);
    return "paper/show";
  }

  /**
   * 转发到新的试卷模板页面
   *
   * @return 新的试卷模板页面
   */
  @GetMapping("/newPaperForm")
  public String addPaperForm() {
    return "paper/newPaperForm";
  }

  /**
   * 试卷题型模版
   *
   * @param paperForm 试卷题型
   * @return 试卷题型页面
   */
  @PostMapping("/newPaperForm")
  public String addPaperForm(PaperForm paperForm) {
    this.paperService.newPaperForm(paperForm);
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
    List<Course> courseList = paperService.findCourseListByTeacherId(teacherId);
    // 专业列表
    List<Major> majors = this.majorService.list();
    // 封装 model 参数
    model.addAttribute("courseList", courseList);
    model.addAttribute("majorList", majors);
    return "paper/newPaper";
  }

  /**
   * 提交组卷信息
   *
   * @param paper 试卷信息
   * @param paperFormId 试卷模板ID
   * @param majorId 所属专业ID
   * @param r r 重定向对象
   * @return 组卷页面
   */
  @PostMapping("/newPaper/{paperFormId}")
  public String add(
      Paper paper, @PathVariable Integer paperFormId, Integer majorId, RedirectAttributes r) {
    // 获取 session 对象
    HttpSession session = HttpContextUtil.getSession();
    // 设置试卷模板 ID
    paper.setPaperFormId(paperFormId);
    // 设置试卷专业 ID
    paper.setMajorId(majorId);
    try {
      // 调用组卷接口
      paperService.newPaper(paper);
      return "redirect:/teacher/paper/show/" + paper.getId();
    } catch (ServiceException e) {
      log.error(ExceptionUtil.stacktraceToString(e));
      r.addFlashAttribute("message", e.getMessage());
      Teacher teacher = (Teacher) session.getAttribute(SysConsts.SESSION.TEACHER);
      return "redirect:/teacher/home/" + teacher.getId();
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
    List<PaperForm> formList = paperService.findAllPaperForm();
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
      paperService.delPaperFormById(id);
      return R.success();
    } catch (Exception e) {
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
    paperService.delPaperById(id);
    return "redirect:/teacher/paper";
  }
}
