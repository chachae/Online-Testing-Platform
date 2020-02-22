package com.exam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.exam.entity.Major;
import com.exam.entity.vo.MajorVo;
import com.github.pagehelper.PageInfo;

/**
 * @author yzn
 * @since 2020/2/8 14:26
 */
public interface MajorService extends IService<Major> {

  /**
   * 分页查询专业
   *
   * @param pageNo 当前页
   * @param major 专业数据
   * @return 专业分页结果集
   */
  PageInfo<MajorVo> pageForMajorList(Integer pageNo, Major major);
}
