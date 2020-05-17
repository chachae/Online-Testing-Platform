package com.chachae.exam.rest;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chachae.exam.common.base.R;
import com.chachae.exam.common.model.Announce;
import com.chachae.exam.core.annotation.Permissions;
import com.chachae.exam.service.AnnounceService;
import java.util.Map;
import javax.annotation.Resource;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 公告控制层
 *
 * @author chachae
 * @since 2020/2/27 23:11
 */
@RestController
@RequestMapping("/api/announce")
public class AnnounceController {

  @Resource
  private AnnounceService announceService;

  /**
   * 分页查询公告
   *
   * @param page 分页信息
   * @return 分页结果集
   */
  @GetMapping("/list")
  public Map<String, Object> pageInfo(Page<Announce> page) {
    return this.announceService.pageForAnnounce(page);
  }

  /**
   * 新增一个公告
   *
   * @param announce 公告信息
   * @return 公告页面
   */
  @PostMapping("/save")
  @Permissions("announce:save")
  public R save(@Valid Announce announce) {
    // 调用新增公告接口
    this.announceService.save(announce);
    return R.success();
  }

  /**
   * 删除单个公告
   *
   * @param id 公告ID
   * @return 成功信息
   */
  @GetMapping("/delete/{id}")
  @Permissions("announce:delete")
  public R delete(@PathVariable Integer id) {
    // 调用公告删除接口
    this.announceService.removeById(id);
    return R.success();
  }

  /**
   * 更新单个公告
   *
   * @param announce 公告信息
   * @return 成功信息
   */
  @PostMapping("/update")
  @Permissions("announce:update")
  public R update(@Valid Announce announce) {
    // 调用公告删除接口
    this.announceService.updateById(announce);
    return R.success();
  }

  /**
   * 获取单个公告信息
   *
   * @param id 公告ID
   * @return 公告信息
   */
  @GetMapping("/{id}")
  @Permissions("announce:list")
  public Announce selectOne(@PathVariable Integer id) {
    return this.announceService.getById(id);
  }
}
