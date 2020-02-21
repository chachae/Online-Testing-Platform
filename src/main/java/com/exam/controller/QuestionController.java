package com.exam.controller;

import cn.hutool.core.util.StrUtil;
import com.exam.common.Page;
import com.exam.common.R;
import com.exam.constant.SysConsts;
import com.exam.entity.Course;
import com.exam.entity.Question;
import com.exam.entity.Type;
import com.exam.exception.ServiceException;
import com.exam.service.CourseService;
import com.exam.service.QuestionService;
import com.exam.service.TypeService;
import com.exam.util.HttpContextUtil;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
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

  @Resource private TypeService typeService;
  @Resource private CourseService courseService;
  @Resource private QuestionService questionService;

  /**
   * 试题 List 集合
   *
   * @param page 当前页
   * @param model model 对象
   * @return 试题集合页面
   */
  @GetMapping
  public String list(Page page, Model model, String courseId) {
    Integer cid = StrUtil.isBlank(courseId) ? null : Integer.parseInt(courseId);
    // 分页查询试题接口
    PageInfo<Question> pageInfo = questionService.pageForQuestionList(page.getPageNo(), cid);
    // 设置 model 对象信息
    model.addAttribute("page", pageInfo);
    // 课程集合
    int id = (int) HttpContextUtil.getSession().getAttribute(SysConsts.SESSION.TEACHER_ID);
    List<Course> courses = this.courseService.listByTeacherId(id);
    model.addAttribute("courseList", courses);
    // 当前选中课程
    if (courseId != null) {
      model.addAttribute("curCourseId", courseId);
    }
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
    Question question = this.questionService.getById(id);
    // 通过课程 ID 查询课程信息
    Course course = this.courseService.getById(question.getCourseId());
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
    // 获取 session 对象
    HttpSession session = HttpContextUtil.getSession();
    // 调用试题类型集合借口
    List<Type> typeList = this.typeService.list();
    // 通过 session 获取教师的ID
    Integer teacherId = (Integer) session.getAttribute(SysConsts.SESSION.TEACHER_ID);
    // 通过教师 ID 获取该老师的课程集合
    List<Course> courseList = questionService.selectCourseByTeacherId(teacherId);
    // 设置 model 对象信息
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
    // 调用试题新增接口
    questionService.save(question);
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
    // 调用通过试题ID查询实体信息接口
    Question question = this.questionService.getById(id);
    model.addAttribute("question", question);
    return "question/edit";
  }

  /**
   * 提交试题信息
   *
   * @param id 试题ID
   * @param question 问题信息
   * @return 试题页面
   */
  @PostMapping("/edit/{id}")
  public String edit(@PathVariable Integer id, Question question) {
    // 更新试题信息
    question.setId(id);
    questionService.updateById(question);
    return "/question/show/" + id;
  }

  /**
   * 删除试题
   *
   * @param id 试题ID
   * @return 试题页面
   */
  @ResponseBody
  @PostMapping("/delete/{id}")
  public R delete(@PathVariable Integer id) {
    try {
      // 通过 ID 移除试题
      questionService.deleteById(id);
      return R.success();
    } catch (ServiceException e) {
      return R.error(e.getMessage());
    }
  }

  /**
   * 导入试题
   *
   * @param multipartFile MultipartFile 对象
   * @return 导入题目结果
   */
  @ResponseBody
  @PostMapping("/import")
  public R importQuestion(@RequestParam("file") MultipartFile multipartFile) {
    try {
      // 调用试题导入接口
      this.questionService.importQuestion(multipartFile);
      return R.success();
    } catch (ServiceException e) {
      return R.error(e.getMessage());
    }
  }
}
