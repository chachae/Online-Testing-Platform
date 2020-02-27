package com.exam.controller;

import com.exam.entity.Course;
import com.exam.service.CourseService;
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
@RequestMapping("/course")
public class CourseController {

  @Resource private CourseService courseService;

  @GetMapping("/teacher/{teacherId}")
  public List<Course> listByTeacherId(@PathVariable Integer teacherId) {
    return this.courseService.listByTeacherId(teacherId);
  }
}
