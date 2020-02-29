package com.exam.rest;

import com.exam.common.R;
import com.exam.entity.Question;
import com.exam.entity.vo.QuestionVo;
import com.exam.service.QuestionService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * @author chachae
 * @since 2020/2/29 21:02
 */
@RestController
@RequestMapping("/api/question")
public class QuestionRest {

  @Resource private QuestionService questionService;

  @GetMapping("/{id}")
  public QuestionVo getOne(@PathVariable Integer id) {
    return this.questionService.selectVoById(id);
  }

  /**
   * 添加试题
   *
   * @param question 问题信息
   * @return 当前的试题页面
   */
  @PostMapping("/save")
  public R add(Question question) {
    // 调用试题新增接口
    this.questionService.save(question);
    return R.success();
  }

  /**
   * 提交试题信息
   *
   * @param question 问题信息
   * @return 试题页面
   */
  @PostMapping("/update")
  public R edit(Question question) {
    // 更新试题信息
    this.questionService.updateById(question);
    return R.success();
  }

  /**
   * 删除试题
   *
   * @param id 试题ID
   * @return 试题页面
   */
  @PostMapping("/delete/{id}")
  public R delete(@PathVariable Integer id) {
    // 通过 ID 移除试题
    questionService.deleteById(id);
    return R.success();
  }

  /**
   * 导入试题
   *
   * @param multipartFile MultipartFile 对象
   * @return 导入题目结果
   */
  @PostMapping("/import")
  public R importQuestion(@RequestParam("file") MultipartFile multipartFile) {
    // 调用试题导入接口
    this.questionService.importQuestion(multipartFile);
    return R.success();
  }
}
