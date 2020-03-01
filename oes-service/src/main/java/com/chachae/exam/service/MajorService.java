package com.chachae.exam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chachae.exam.common.entity.Major;
import com.chachae.exam.common.entity.vo.MajorVo;
import com.github.pagehelper.PageInfo;

import java.util.List;

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

  /**
   * 通过学院 ID 获取专业集合
   *
   * @param academyId 学院 ID
   * @return 集合信息
   */
  List<Major> listByAcademyId(Integer academyId);
}
