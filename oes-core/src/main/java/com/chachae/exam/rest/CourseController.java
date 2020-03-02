package com.chachae.exam.rest;

import com.chachae.exam.common.base.R;
import com.chachae.exam.common.model.Course;
import com.chachae.exam.core.annotation.Permissions;
import com.chachae.exam.service.CourseService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author chachae
 * @since 2020/2/27 16:25
 */
@RestController
@RequestMapping("/api/course")
public class CourseController {

  @Resource private CourseService courseService;

  @GetMapping("/teacher/{teacherId}")
  @Permissions("course:list")
  public List<Course> listByTeacherId(@PathVariable Integer teacherId) {
    return this.courseService.listByTeacherId(teacherId);
  }

  /**
   * 添加课程
   *
   * @param courseName 课程名称
   * @param teacherId 教师ID
   * @return 成功信息
   */
  @GetMapping("/save")
  @Permissions("course:save")
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
  @GetMapping("/delete/{id}")
  @Permissions("course:delete")
  public R delCourse(@PathVariable Integer id) {
    // 调用课程删除接口
    this.courseService.removeById(id);
    return R.success();
  }
}
