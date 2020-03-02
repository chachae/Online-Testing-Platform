package com.chachae.exam.rest;

import com.chachae.exam.common.base.R;
import com.chachae.exam.common.constant.SysConsts;
import com.chachae.exam.common.model.PaperForm;
import com.chachae.exam.common.exception.ServiceException;
import com.chachae.exam.core.annotation.Permissions;
import com.chachae.exam.service.PaperFormService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author chachae
 * @since 2020/2/29 16:24
 */
@RestController
@RequestMapping("/api/paperForm")
public class PaperFormController {

  @Resource private PaperFormService paperFormService;

  /**
   * 删除模版不跳转页面
   *
   * @param id 试卷模板ID
   * @return 当前试卷模板页面
   */
  @GetMapping("/delete/{id}")
  @Permissions("paperForm:delete")
  public R delPaperForm(@PathVariable Integer id) {
    try {
      // 调用试卷模板已出接口（通过ID删除）
      this.paperFormService.removeById(id);
      return R.success();
    } catch (ServiceException e) {
      return R.error(e.getMessage());
    }
  }

  /**
   * 试卷题型模版
   *
   * @param paperForm 试卷题型
   * @return 试卷题型页面
   */
  @PostMapping("/save")
  @Permissions("paperForm:save")
  public R addPaperForm(PaperForm paperForm) {
    // 设置模板类型
    paperForm.setType(SysConsts.PaperForm.INSERT);
    // 调用试卷模板新增接口
    this.paperFormService.save(paperForm);
    return R.successWithData(paperForm.getId());
  }
}
