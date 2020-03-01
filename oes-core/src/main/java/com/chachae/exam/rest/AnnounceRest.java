package com.chachae.exam.rest;

import com.chachae.exam.common.base.R;
import com.chachae.exam.common.entity.Announce;
import com.chachae.exam.service.AnnounceService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 公告控制层
 *
 * @author chachae
 * @since 2020/2/27 23:11
 */
@RestController("announceRest")
@RequestMapping("/api/announce")
public class AnnounceRest {

  @Resource private AnnounceService announceService;

  /**
   * 新增一个公告
   *
   * @param announce 公告信息
   * @return 公告页面
   */
  @PostMapping("/save")
  public R save(Announce announce) {
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
  public R update(Announce announce) {
    // 调用公告删除接口
    this.announceService.updateById(announce);
    return R.success();
  }

  /**
   * 批量删除公告
   *
   * @param ids 公告ID集合，<a href="http://localhost:8080/announce/del?ids=1,2,3,4"
   * @return 成功信息
   */
  @PostMapping("/delete")
  public R deleteBatch(Integer[] ids) {
    // 调用公告批量删除接口
    this.announceService.delete(ids);
    return R.success();
  }

  /**
   * 获取单个公告信息
   *
   * @param id 公告ID
   * @return 公告信息
   */
  @GetMapping("/{id}")
  public Announce selectOne(@PathVariable Integer id) {
    return this.announceService.getById(id);
  }
}
