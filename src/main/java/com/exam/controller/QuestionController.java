package com.exam.controller;

import com.exam.common.Page;
import com.exam.constant.SysConsts;
import com.exam.entity.Course;
import com.exam.entity.Question;
import com.exam.entity.Type;
import com.exam.exception.ServiceException;
import com.exam.service.QuestionService;
import com.exam.util.HttpContextUtil;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 问题控制层
 *
 * @author yzn
 * @date 2020/1/6
 */
@Controller
@RequestMapping("/teacher/question")
public class QuestionController {

  @Resource private QuestionService questionService;

  /**
   * 试卷 List 集合
   *
   * @param page 当前页
   * @param model model 对象
   * @return 试卷集合页面
   */
  @GetMapping
  public String list(Page page, Model model) {
    PageInfo<Question> pageInfo = questionService.pageForQuestionList(page.getPageNo());
    model.addAttribute("page", pageInfo);
    return "question/list";
  }

  /**
   * 根据ID 查询试题信息
   *
   * @param id 试题 ID
   * @param model model 对象
   * @return 试题信息页面
   */
  @GetMapping("/show/{id}")
  public String show(@PathVariable Integer id, Model model) {
    // 调用问题查询接口
    Question question = questionService.findById(id);
    // 通过课程 ID 查询课程信息
    Course course = questionService.findByCourseId(question.getCourseId());
    model.addAttribute("question", question);
    model.addAttribute("course", course);
    return "question/show";
  }

  /**
   * 新增试题
   *
   * @param model model 对象
   * @return 试题界面
   */
  @GetMapping("/new")
  public String add(Model model) {
    HttpSession session = HttpContextUtil.getSession();
    List<Type> typeList = questionService.findAllType();
    Integer teacherId = (Integer) session.getAttribute(SysConsts.SESSION.TEACHER_ID);
    List<Course> courseList = questionService.findTeacherCourse(teacherId);
    model.addAttribute("typeList", typeList);
    model.addAttribute("courseList", courseList);
    return "question/new";
  }

  /**
   * 添加实体
   *
   * @param question 问题信息
   * @param r r 重定向对象
   * @return 当前的试题页面
   */
  @PostMapping("/new")
  public String add(Question question, RedirectAttributes r) {
    questionService.saveNewQuestion(question);
    r.addFlashAttribute("message", "试题添加成功！");
    return "redirect:/teacher/question/show/" + question.getId();
  }

  /**
   * 修改试题信息
   *
   * @param id 实体ID
   * @param model model 对象
   * @return 实体编辑页面
   */
  @GetMapping("/edit/{id}")
  public String edit(@PathVariable Integer id, Model model) {
    Question question = questionService.findById(id);
    model.addAttribute("question", question);
    return "question/edit";
  }

  /**
   * 提交试题信息
   *
   * @param id 试题ID
   * @param question 问题信息
   * @param r r 对象
   * @return 试题页面
   */
  @PostMapping("/edit/{id}")
  public String edit(Question question, @PathVariable Integer id, RedirectAttributes r) {
    try {
      questionService.editQuestion(
          id, question.getQuestionName(), question.getAnswer(), question.getRemark());
      r.addFlashAttribute("message", "修改成功");
      return "redirect:/teacher/question/show/" + id;
    } catch (ServiceException e) {
      r.addFlashAttribute("message", e.getMessage());
      return "redirect:/teacher/question";
    }
  }

  /**
   * 删除试题
   *
   * @param id 试题ID
   * @return 试题页面
   */
  @GetMapping("/delete/{id}")
  public String delete(@PathVariable Integer id) {
    questionService.deleteQuestion(id);
    return "redirect:/teacher/question";
  }
}
