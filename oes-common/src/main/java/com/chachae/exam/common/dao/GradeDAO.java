package com.chachae.exam.common.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chachae.exam.common.model.Grade;
import com.chachae.exam.common.model.dto.QueryGradeDto;
import com.chachae.exam.common.model.vo.GradeVo;
import org.apache.ibatis.annotations.Param;

/**
 * (Grade)表数据库访问层
 *
 * @author chachae
 * @since 2020-05-18 21:26:47
 */
public interface GradeDAO extends BaseMapper<Grade> {

  /**
   * 通过条件查询班级 List 集合
   *
   * @param page   分页信息
   * @param entity 班级模糊信息
   * @return 班级 List 集合
   */
  IPage<GradeVo> pageVo(Page<Grade> page, @Param("entity") QueryGradeDto entity);
}