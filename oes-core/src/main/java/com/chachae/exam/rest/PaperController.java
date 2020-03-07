package com.chachae.exam.rest;

import cn.hutool.core.util.StrUtil;

import com.chachae.exam.common.base.R;
import com.chachae.exam.common.constant.SysConsts;
import com.chachae.exam.common.model.Paper;
import com.chachae.exam.common.model.Question;
import com.chachae.exam.common.model.StuAnswerRecord;
import com.chachae.exam.common.model.dto.AnswerEditDto;
import com.chachae.exam.common.model.dto.ImportPaperDto;
import com.chachae.exam.common.model.dto.ImportPaperRandomQuestionDto;
import com.chachae.exam.common.model.dto.PaperQuestionUpdateDto;
import com.chachae.exam.common.util.HttpContextUtil;
import com.chachae.exam.core.annotation.Permissions;
import com.chachae.exam.service.PaperService;
import com.chachae.exam.service.QuestionService;
import com.chachae.exam.service.ScoreService;
import com.chachae.exam.service.StuAnswerRecordService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * @author chachae
 * @since 2020/2/29 12:11
 */
@RestController
@RequestMapping("/api/paper")
public class PaperController {

  @Resource private ScoreService scoreService;
  @Resource private StuAnswerRecordService stuAnswerRecordService;
  @Resource private QuestionService questionService;
  @Resource private PaperService paperService;

  @PostMapping("/update")
  @Permissions("paper:update")
  public R updateTime(Paper paper) {
    this.paperService.updateById(paper);
    return R.success();
  }

  @GetMapping("/{id}")
  @Permissions("paper:list")
  public Paper getOne(@PathVariable Integer id) {
    return this.paperService.getById(id);
  }

  @PostMapping("/import/excel")
  @Permissions("paper:import")
  public R excel(@RequestParam("file") MultipartFile multipartFile) {
    // 导入试卷
    ImportPaperDto dto = this.questionService.importPaper(multipartFile);
    return R.successWithData(dto);
  }

  @PostMapping("/save/import")
  @Permissions("paper:save")
  public R newPaperByExcel(Paper paper, ImportPaperRandomQuestionDto entity) {
    // 获取教师 ID
    Integer teacherId = (Integer) HttpContextUtil.getAttribute(SysConsts.Session.TEACHER_ID);
    // 设置出卷老师
    paper.setTeacherId(teacherId);
    // 局部随机参数判断，没有局部随机参数则调用普通的插入接口
    boolean a = entity.getA() == 0,
        b = entity.getB() == 0,
        c = entity.getC() == 0,
        d = entity.getD() == 0,
        e = entity.getE() == 0,
        f = entity.getF() == 0;
    if (a && b && c && d && e && f) {
      this.paperService.save(paper);
    } else {
      this.paperService.saveWithImportPaper(paper, entity);
    }
    return R.successWithData(paper.getId());
  }

  /**
   * 提交组卷信息
   *
   * @param paper 试卷信息
   * @param paperFormId 试卷模板ID
   * @return 组卷页面
   */
  @PostMapping("/save/random")
  @Permissions("paper:save")
  public R add(Paper paper, Integer paperFormId, String difficulty) {
    // 设置试卷模板 ID
    paper.setPaperFormId(paperFormId);
    // 获取教师 ID
    int teacherId = (int) HttpContextUtil.getAttribute(SysConsts.Session.TEACHER_ID);
    // 调用组卷接口
    paper.setTeacherId(teacherId);
    // 判断是否指定难度
    if (StrUtil.isBlank(difficulty)) {
      paperService.randomNewPaper(paper);
    } else {
      // 带指定难度的接口
      paperService.randomNewPaper(paper, difficulty);
    }
    return R.successWithData(paper.getId());
  }

  /**
   * 修改主观题成绩
   *
   * @param dto 信息
   */
  @PostMapping("/update/score")
  @Permissions("paper:update")
  public R editScore(AnswerEditDto dto) {
    // 修改该题得分
    StuAnswerRecord record = new StuAnswerRecord();
    record.setId(dto.getId()).setScore(dto.getNewScore());
    this.stuAnswerRecordService.updateById(record);
    // 修改总分
    StuAnswerRecord stuRec = this.stuAnswerRecordService.getById(dto.getId());
    // 封装参数
    dto.setStuId(stuRec.getStuId());
    dto.setPaperId(stuRec.getPaperId());
    this.scoreService.updateScoreByStuIdAndPaperId(dto);
    return R.success();
  }

  /**
   * 级联删除试卷、分数、答案记录
   *
   * @param id 试卷ID
   * @return 试卷页面
   */
  @PostMapping("/delete/{id}")
  @Permissions("paper:delete")
  public R delPaper(@PathVariable Integer id) {
    // 级联删除试卷（详见接口实现类）
    paperService.deletePaperById(id);
    return R.success();
  }

  /**
   * 修改試卷题目
   *
   * @param dto 修改的信息
   * @return 回调信息
   */
  @PostMapping("/update/question")
  @Permissions("paper:update")
  public R updateQuestionId(PaperQuestionUpdateDto dto) {
    this.paperService.updateQuestionId(dto);
    return R.success();
  }
}
