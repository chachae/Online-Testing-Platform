package com.chachae.exam.common.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chachae.exam.common.model.Major;
import com.chachae.exam.common.model.vo.MajorVo;
import org.apache.ibatis.annotations.Param;

/**
 * 专业 Mapper 接口
 *
 * @author chachae
 * @date 2020/1/19
 */
public interface MajorDAO extends BaseMapper<Major> {

  /**
   * 查询专业 List 集合
   *
   * @param major 专业查询条件
   * @return 专业 List 集合
   */
  IPage<MajorVo> pageVo(Page<Major> page, @Param("major") Major major);
}
