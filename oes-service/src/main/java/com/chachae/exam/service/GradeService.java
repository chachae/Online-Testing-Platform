package com.chachae.exam.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chachae.exam.common.model.Grade;
import com.chachae.exam.common.model.dto.ImportGradeDto;
import com.chachae.exam.common.model.dto.QueryGradeDto;
import com.chachae.exam.common.model.vo.GradeVo;
import java.util.List;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

/**
 * (Grade)表服务接口
 *
 * @author chachae
 * @since 2020-05-18 21:26:48
 */
public interface GradeService extends IService<Grade> {

  /**
   * 通过专业id查询班级
   *
   * @param majorId 专业id
   * @return 班级集合
   */
  List<Grade> listByMajorId(Integer majorId);

  /**
   * 分页查询班级信息
   *
   * @param page   分页条件
   * @param entity 模糊搜索条件
   * @return 分页信息
   */
  Map<String, Object> listPage(Page<Grade> page, QueryGradeDto entity);

  /**
   * 增加班级
   *
   * @param entity 班级信息
   */
  void save(ImportGradeDto entity);

  /**
   * 获取 vo 对象
   *
   * @param id 班级id
   * @return 班级vo
   */
  GradeVo selectVoById(Integer id);


  /**
   * 导入班级数据
   *
   * @param multipartFile /
   */
  void importMajorsExcel(MultipartFile multipartFile);

}