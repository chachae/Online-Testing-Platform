package com.exam.controller;

import com.exam.common.R;
import com.exam.constant.SysConsts;
import com.exam.entity.Course;
import com.exam.service.CourseService;
import com.exam.service.ScoreService;
import com.exam.util.HttpContextUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * 分数控制层
 *
 * @author yzn
 * @date 2020/2/1
 */
@Controller
@RequestMapping("/score")
public class ScoreController {

  @Resource private ScoreService scoreService;
  @Resource private CourseService courseService;

  /**
   * 课程列表
   *
   * @param model model 对象
   * @return 课程页面
   */
  @GetMapping("/course")
  public String chart(Model model) {
    // 获取 session 对象
    HttpSession session = HttpContextUtil.getSession();
    // 获取课程 List 几何
    List<Course> courseList =
        courseService.listByTeacherId((Integer) session.getAttribute(SysConsts.SESSION.TEACHER_ID));
    model.addAttribute("courseList", courseList);
    return "teacher/chart";
  }

  /**
   * 课程成绩统计
   *
   * @param courseId 课程ID
   * @param model model 对象
   * @return 统计页面
   */
  @GetMapping("/courseChart")
  public String courseChart(Integer courseId, Model model) {
    // 通过课程 ID 获取课程信息
    Course course = courseService.getById(courseId);
    model.addAttribute("course", course);
    return "teacher/scoreCourse";
  }

  /**
   * 根据课程id统计所有试卷考试学生成绩
   *
   * @param id courseId 课程ID
   * @return 成功信息
   */
  @PostMapping("/courseChart/{id}")
  @ResponseBody
  public R courseChart(@PathVariable Integer id) {
    List<Map<String, Object>> scoreCount = scoreService.countByCourseId(id);
    return R.successWithData(scoreCount);
  }

  /**
   * 转发学生分数雷达页面
   *
   * @return 学生分数雷达页面
   */
  @GetMapping("/student/chart")
  public String studentChart() {
    return "student/scoreChart";
  }

  /**
   * 雷达图 成绩与平均成绩比较
   *
   * @return 成绩与平均成绩比较页面
   */
  @PostMapping("/student/chart/{id}")
  @ResponseBody
  public R stuChart(@PathVariable Integer id) {
    List<Map<String, Object>> aveAndMyScoreList = scoreService.averageScore(id);
    return R.successWithData(aveAndMyScoreList);
  }

  /**
   * 统计该学生成绩分布
   *
   * @return 统计该学生成绩分布页面
   */
  @GetMapping("/student/chart2")
  @ResponseBody
  public R stuSelfChart() {
    // 获取 session 对象
    HttpSession session = HttpContextUtil.getSession();
    // 成绩分布集合
    List<Map<String, Object>> everyScoreList =
        scoreService.countByLevel((Integer) session.getAttribute(SysConsts.SESSION.STUDENT_ID));
    return R.successWithData(everyScoreList);
  }
}
