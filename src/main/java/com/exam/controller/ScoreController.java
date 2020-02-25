package com.exam.controller;

import com.exam.common.R;
import com.exam.constant.SysConsts;
import com.exam.service.CourseService;
import com.exam.service.ScoreService;
import com.exam.util.HttpContextUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

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
    // 教师ID
    int id = (Integer) HttpContextUtil.getAttribute(SysConsts.SESSION.TEACHER_ID);
    // 设置课程的 model 信息
    model.addAttribute("courseList", courseService.listByTeacherId(id));
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
    model.addAttribute("course", courseService.getById(courseId));
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
    return R.successWithData(scoreService.countByCourseId(id));
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
    return R.successWithData(scoreService.averageScore(id));
  }

  /**
   * 统计该学生成绩分布
   *
   * @return 统计该学生成绩分布页面
   */
  @GetMapping("/student/chart2")
  @ResponseBody
  public R stuSelfChart() {
    // 获取学生的 ID
    int stuId = (Integer) HttpContextUtil.getAttribute(SysConsts.SESSION.STUDENT_ID);
    // 设置成绩分布集合的 model 对象信息
    return R.successWithData(scoreService.countByLevel(stuId));
  }
}
