package com.chachae.exam.rest;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chachae.exam.common.base.R;
import com.chachae.exam.common.model.Course;
import com.chachae.exam.core.annotation.Permissions;
import com.chachae.exam.service.CourseService;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author chachae
 * @since 2020/2/27 16:25
 */
@RestController
@RequestMapping("/api/course")
public class CourseController {

  @Resource
  private CourseService courseService;

  @GetMapping("/teacher/{teacherId}")
  @Permissions("course:list")
  public List<Course> listByTeacherId(@PathVariable Integer teacherId) {
    return this.courseService.listByTeacherId(teacherId);
  }

  @GetMapping("/list")
  @Permissions("course:list")
  public Map<String, Object> pageCourse(Page<Course> page, Integer teacherId) {
    return this.courseService.listPage(page, teacherId);
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
   * 更新课程
   *
   * @return 成功信息
   */
  @PostMapping("/update")
  public R updateCourse(@Valid Course course) {
    this.courseService.update(course);
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

  /**
   * 查看课程
   *
   * @param id 课程ID
   * @return 删除成功信息
   */
  @GetMapping("/{id}")
  public Course getCourse(@PathVariable Integer id) {
    return this.courseService.getById(id);
  }
}
