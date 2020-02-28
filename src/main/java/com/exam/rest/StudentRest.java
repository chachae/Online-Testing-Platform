package com.exam.rest;

import com.exam.common.R;
import com.exam.entity.Student;
import com.exam.service.StudentService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author chachae
 * @since 2020/2/28 22:31
 */
@RestController
@RequestMapping("/api/student")
public class StudentRest {

  @Resource private StudentService studentService;

  /**
   * 更新学生
   *
   * @param student 学生信息
   * @return 成功信息
   */
  @PostMapping("/update")
  public R updateStudent(Student student) {
    this.studentService.updateById(student);
    return R.success();
  }

  /**
   * 删除学生
   *
   * @param id 学生ID
   * @return 成功信息
   */
  @PostMapping("/delete/{id}")
  public R deleteStudent(@PathVariable Integer id) {
    this.studentService.removeById(id);
    return R.success();
  }

  /**
   * 增加学生
   *
   * @param student 学生信息
   * @return 成功信息
   */
  @PostMapping("/save")
  public R saveStudent(Student student) {
    // 调用增加接口，并捕捉学号存在的异常
    this.studentService.save(student);
    return R.success();
  }
}
