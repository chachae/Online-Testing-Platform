package com.chachae.exam.rest;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chachae.exam.common.base.R;
import com.chachae.exam.common.constant.SysConsts;
import com.chachae.exam.common.model.Score;
import com.chachae.exam.common.model.vo.StudentVo;
import com.chachae.exam.common.util.HttpUtil;
import com.chachae.exam.core.annotation.Permissions;
import com.chachae.exam.service.ScoreService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author chachae
 * @since 2020/3/1 10:23
 */
@RestController
@RequestMapping("/api/score")
public class ScoreController {

  @Resource private ScoreService scoreService;

  /**
   * 雷达图 成绩与平均成绩比较
   *
   * @return 成绩与平均成绩比较页面
   */
  @PostMapping("/student/chart/{id}")
  @Permissions("score:chart")
  public R stuChart(@PathVariable Integer id) {
    return R.successWithData(scoreService.averageScore(id));
  }

  /**
   * 统计该学生成绩分布
   *
   * @return 统计该学生成绩分布页面
   */
  @GetMapping("/student/chart2")
  @Permissions("score:chart")
  public R stuSelfChart() {
    // 获取学生的 ID
    StudentVo student = (StudentVo) HttpUtil.getAttribute(SysConsts.Session.STUDENT);
    // 设置成绩分布集合的 model 对象信息
    return R.successWithData(scoreService.countByLevel(student.getId()));
  }

  /**
   * 获取学生乘机分业数据
   *
   * @return 获取学生乘机分业数据
   */
  @GetMapping("/list")
  @Permissions("score:list")
  public Map<String, Object> pageScore(Page<Score> page) {
    // 获取学生的 ID
    StudentVo student = (StudentVo) HttpUtil.getAttribute(SysConsts.Session.STUDENT);
    return this.scoreService.pageByStuId(page, student.getId());
  }
}
