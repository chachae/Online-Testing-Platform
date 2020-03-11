package com.chachae.exam.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chachae.exam.common.model.Announce;

import java.util.Map;

/**
 * 公告业务接口
 *
 * @author chachae
 * @since 2020/2/14 17:42
 */
public interface AnnounceService extends IService<Announce> {

  /**
   * 分页查询公告
   *
   * @param page 当前页
   * @return 分页结果集
   */
  Map<String, Object> pageForAnnounce(Page<Announce> page);
}
