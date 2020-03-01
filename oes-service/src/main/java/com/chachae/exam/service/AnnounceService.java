package com.chachae.exam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chachae.exam.common.entity.Announce;

/**
 * 公告业务接口
 *
 * @author yzn
 * @since 2020/2/14 17:42
 */
public interface AnnounceService extends IService<Announce> {

  /**
   * 批量删除公告
   *
   * @param ids 公告ID集合
   */
  void delete(Integer[] ids);
}
