package com.chachae.exam.rest;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chachae.exam.common.base.R;
import com.chachae.exam.common.model.Question;
import com.chachae.exam.common.model.dto.QuestionQueryDto;
import com.chachae.exam.common.model.vo.QuestionVo;
import com.chachae.exam.core.annotation.Limit;
import com.chachae.exam.core.annotation.Permissions;
import com.chachae.exam.service.QuestionService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.Map;

/**
 * The type Question controller.
 *
 * @author chachae
 * @since 2020 /2/29 21:02
 */
@RestController
@RequestMapping("/api/question")
public class QuestionController {

  /** The Question service. */
  @Resource private QuestionService questionService;

  @GetMapping("/list")
  @Permissions("course:list")
  @Limit(key = "courseList", period = 3, count = 18, name = "试题查询接口", prefix = "limit")
  public Map<String, Object> listPage(Page<Question> page, QuestionQueryDto entity) {
    return this.questionService.listPage(page, entity);
  }

  /**
   * Gets one.
   *
   * @param id the id
   * @return the one
   */
  @GetMapping("/{id}")
  @Permissions("course:list")
  public QuestionVo getOne(@PathVariable Integer id) {
    return this.questionService.selectVoById(id);
  }

  /**
   * 添加试题
   *
   * @param question 问题信息
   * @return 当前的试题页面 r
   */
  @PostMapping("/save")
  @Permissions("question:save")
  public R add(Question question) {
    // 调用试题新增接口
    this.questionService.save(question);
    return R.success();
  }

  /**
   * 提交试题信息
   *
   * @param question 问题信息
   * @return 试题页面 r
   */
  @PostMapping("/update")
  @Permissions("question:update")
  public R edit(Question question) {
    // 更新试题信息
    this.questionService.updateById(question);
    return R.success();
  }

  /**
   * 删除试题
   *
   * @param id 试题ID
   * @return 试题页面 r
   */
  @PostMapping("/delete/{id}")
  @Permissions("question:delete")
  public R delete(@PathVariable Integer id) {
    // 通过 ID 移除试题
    questionService.deleteById(id);
    return R.success();
  }

  /**
   * 导入试题
   *
   * @param multipartFile MultipartFile 对象
   * @return 导入题目结果 r
   */
  @PostMapping("/import")
  @Permissions("question:import")
  public R importQuestion(@RequestParam("file") MultipartFile multipartFile) {
    // 调用试题导入接口
    this.questionService.importQuestion(multipartFile);
    return R.success();
  }

  /**
   * 修改試卷题目答案
   *
   * @param question 问题信息
   * @return 回调信息 r
   */
  @PostMapping("/update/answer")
  @Permissions("question:update")
  public R updateAnswer(Question question) {
    System.out.println(question);
    this.questionService.updateById(question);
    return R.success();
  }
}
