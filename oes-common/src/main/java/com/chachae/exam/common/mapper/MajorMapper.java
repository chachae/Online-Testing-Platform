package com.chachae.exam.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chachae.exam.common.entity.Major;
import com.chachae.exam.common.entity.vo.MajorVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 专业 Mapper 接口
 *
 * @author yzn
 * @date 2020/1/19
 */
public interface MajorMapper extends BaseMapper<Major> {

  /**
   * 查询专业 List 集合
   *
   * @param major 专业查询条件
   * @return 专业 List 集合
   */
  List<MajorVo> listVo(Major major);
}
