package com.chachae.exam.rest;

import com.chachae.exam.common.base.R;
import com.chachae.exam.common.model.Course;
import com.chachae.exam.core.annotation.Permissions;
import com.chachae.exam.service.CourseService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
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
   * @return 成功信息
   */
  @PostMapping("/save")
  @Permissions("course:save")
  public R newCourse(@Valid Course course) {
    this.courseService.save(course);
    return R.success();
  }

  /**
   * 删除课程
   *
   * @param id 课程ID
   * @return 删除成功信息
   */
  @PostMapping("/delete/{id}")
  @Permissions("course:delete")
  public R delCourse(@PathVariable Integer id) {
    // 调用课程删除接口
    this.courseService.removeById(id);
    return R.success();
  }
}
