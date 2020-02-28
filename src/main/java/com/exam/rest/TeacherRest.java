package com.exam.rest;

import com.exam.common.R;
import com.exam.entity.Teacher;
import com.exam.service.TeacherService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author chachae
 * @since 2020/2/28 12:00
 */
@RestController
@RequestMapping("/api/teacher")
public class TeacherRest {

  @Resource private TeacherService teacherService;

  @GetMapping("/{id}")
  public Teacher getOne(@PathVariable Integer id) {
    return this.teacherService.getById(id);
  }

  /**
   * 更新教师信息
   *
   * @param teacher 教师信息
   * @return 成功信息
   */
  @PostMapping("/update")
  public R updateTeacher(Teacher teacher) {
    // 通过ID关系教师信息
    this.teacherService.updateById(teacher);
    return R.success();
  }

  /**
   * 增加教师
   *
   * @param teacher 教师信息
   * @return 成功信息
   */
  @PostMapping("/save")
  public R saveTeacher(Teacher teacher) {
    // 调用教师信息新增接口
    this.teacherService.save(teacher);
    return R.success();
  }

  /**
   * 删除教师
   *
   * @param id 教师ID
   * @return 回调信息
   */
  @PostMapping("/delete/{id}")
  public R deleteTeacher(@PathVariable Integer id) {
    this.teacherService.removeById(id);
    return R.success();
  }
}
