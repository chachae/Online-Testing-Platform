package com.chachae.exam.rest;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chachae.exam.common.base.R;
import com.chachae.exam.common.constant.SysConsts.Session;
import com.chachae.exam.common.model.Admin;
import com.chachae.exam.common.model.Grade;
import com.chachae.exam.common.model.dto.ImportGradeDto;
import com.chachae.exam.common.model.dto.QueryGradeDto;
import com.chachae.exam.common.util.HttpUtil;
import com.chachae.exam.service.GradeService;
import java.util.Map;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author chachae
 * @since 2020/5/18 22:00
 */

@RestController
@RequestMapping("/api/grade")
@RequiredArgsConstructor
public class GradeController {

  private final GradeService gradeService;

  @GetMapping("/list")
  public Map<String, Object> pageList(Page<Grade> page, QueryGradeDto entity) {
    Admin admin = (Admin) HttpUtil.getAttribute(Session.ADMIN);
    if (admin != null && admin.getAcademyId() != null) {
      entity.setAcademyId(admin.getAcademyId());
    }
    return this.gradeService.listPage(page, entity);
  }

  /**
   * 增加班级
   */
  @PostMapping("/save")
  public R saveGrades(@Valid ImportGradeDto entity) {
    // 调用新增接口
    this.gradeService.save(entity);
    return R.success();
  }

  /**
   * 导入班级
   *
   * @param multipartFile MultipartFile 对象
   * @return 导入班级结果
   */
  @PostMapping("/import")
  public R importQuestion(@RequestParam("file") MultipartFile multipartFile) {
    // 调用试题导入接口
    this.gradeService.importMajorsExcel(multipartFile);
    return R.success();
  }
}
