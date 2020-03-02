package com.chachae.exam.rest;

import com.chachae.exam.common.base.R;
import com.chachae.exam.common.model.Major;
import com.chachae.exam.common.exception.ServiceException;
import com.chachae.exam.core.annotation.Permissions;
import com.chachae.exam.service.MajorService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author chachae
 * @since 2020/2/27 16:02
 */
@RestController
@RequestMapping("/api/major")
public class MajorController {

  @Resource private MajorService majorService;

  @GetMapping
  @Permissions("major:list")
  public List<Major> majorList() {
    return this.majorService.list();
  }

  /**
   * 根据学院 ID 搜索所属的专业集合信息
   *
   * @param academyId 学院 ID
   * @return 专业集合信息
   */
  @GetMapping("/academy/{academyId}")
  @Permissions("major:list")
  public List<Major> majorList(@PathVariable Integer academyId) {
    return this.majorService.listByAcademyId(academyId);
  }

  /**
   * 根据 ID 搜索单个专业信息
   *
   * @param id 专业 ID
   * @return 专业信息
   */
  @GetMapping("/{id}")
  @Permissions("major:list")
  public Major getOne(@PathVariable Integer id) {
    return this.majorService.getById(id);
  }

  /**
   * 更新专业信息
   *
   * @param major 专业信息
   * @return 成功信息
   */
  @ResponseBody
  @PostMapping("/update")
  @Permissions("major:update")
  public R updateMajor(Major major) {
    this.majorService.updateById(major);
    return R.success();
  }

  /**
   * 新增专业信息
   *
   * @param major 专业信息
   * @return 成功信息
   */
  @PostMapping("/save")
  @Permissions("major:save")
  public R saveMajor(Major major) {
    this.majorService.save(major);
    return R.success();
  }

  /**
   * 删除专业信息
   *
   * @param id 专业ID
   * @return 回调信息
   */
  @PostMapping("/delete/{id}")
  @Permissions("major:delete")
  public R deleteMajor(@PathVariable Integer id) {
    try {
      this.majorService.removeById(id);
      return R.success();
    } catch (ServiceException e) {
      return R.error(e.getMessage());
    }
  }
}
