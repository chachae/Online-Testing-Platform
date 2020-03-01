package com.chachae.exam.rest;

import com.chachae.exam.common.base.R;
import com.chachae.exam.common.constant.SysConsts;
import com.chachae.exam.common.util.HttpContextUtil;
import com.chachae.exam.service.ScoreService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author chachae
 * @since 2020/3/1 10:23
 */
@RestController
@RequestMapping("/api/score")
public class ScoreRest {

  @Resource private ScoreService scoreService;

  /**
   * 雷达图 成绩与平均成绩比较
   *
   * @return 成绩与平均成绩比较页面
   */
  @PostMapping("/student/chart/{id}")
  public R stuChart(@PathVariable Integer id) {
    return R.successWithData(scoreService.averageScore(id));
  }

  /**
   * 统计该学生成绩分布
   *
   * @return 统计该学生成绩分布页面
   */
  @GetMapping("/student/chart2")
  public R stuSelfChart() {
    // 获取学生的 ID
    int stuId = (Integer) HttpContextUtil.getAttribute(SysConsts.SESSION.STUDENT_ID);
    // 设置成绩分布集合的 model 对象信息
    return R.successWithData(scoreService.countByLevel(stuId));
  }
}
