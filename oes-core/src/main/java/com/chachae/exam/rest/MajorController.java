package com.chachae.exam.rest;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chachae.exam.common.base.R;
import com.chachae.exam.common.constant.SysConsts.Session;
import com.chachae.exam.common.model.Admin;
import com.chachae.exam.common.model.Major;
import com.chachae.exam.common.util.HttpUtil;
import com.chachae.exam.core.annotation.Limit;
import com.chachae.exam.core.annotation.Permissions;
import com.chachae.exam.service.MajorService;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author chachae
 * @since 2020/2/27 16:02
 */
@RestController
@RequestMapping("/api/major")
public class MajorController {

  @Resource
  private MajorService majorService;

  @GetMapping
  @Permissions("major:list")
  public List<Major> majorList() {
    return this.majorService.list();
  }

  /**
   * 根据学院 ID 分页所属的专业集合信息<br> 接口限流，5秒内允许请求20次
   *
   * @param major 模糊查询条件
   * @return 专业集合信息
   */
  @GetMapping("/list")
  @Permissions("major:list")
  @Limit(key = "majorList", period = 5, count = 20, name = "专业查询接口", prefix = "limit")
  public Map<String, Object> pageList(Page<Major> page, Major major) {
    Admin admin = (Admin) HttpUtil.getAttribute(Session.ADMIN);
    if (admin.getAcademyId() != null) {
      major.setAcademyId(admin.getAcademyId());
    }
    return this.majorService.listPage(page, major);
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
  public R updateMajor(@Valid Major major) {
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
  public R saveMajor(@Valid Major major) {
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
    this.majorService.removeById(id);
    return R.success();
  }

  /**
   * 导入专业
   *
   * @param multipartFile MultipartFile 对象
   * @return 导入专业结果
   */
  @PostMapping("/import")
  public R importMajor(@RequestParam("file") MultipartFile multipartFile) {
    // 调用试题导入接口
    this.majorService.importMajorsExcel(multipartFile);
    return R.success();
  }
}
