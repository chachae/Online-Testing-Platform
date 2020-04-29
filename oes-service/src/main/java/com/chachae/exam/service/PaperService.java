package com.chachae.exam.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chachae.exam.common.model.Paper;
import com.chachae.exam.common.model.dto.ImportPaperRandomQuestionDto;
import com.chachae.exam.common.model.dto.PaperQuestionUpdateDto;
import com.chachae.exam.common.model.dto.QueryPaperDto;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

/**
 * 试卷业务接口
 *
 * @author chachae
 * @date 2020/1/13
 */
public interface PaperService extends IService<Paper> {

  /**
   * 随机组卷
   *
   * @param difficulty 指定难度
   * @param paper      试卷信息
   */
  void randomNewPaper(Paper paper, String difficulty);

  /**
   * 教师批改试卷
   *
   * @param stuId   学生ID
   * @param paperId 试卷ID
   * @param request request 对象.
   */
  void markPaper(Integer stuId, Integer paperId, HttpServletRequest request);

  /**
   * 根据老师Id查找已结束试卷
   *
   * @param teacherId 教师ID
   * @return 该教师所属已完成的试卷
   */
  List<Paper> listDoneByTeacherId(Integer teacherId);

  /**
   * 删除试卷
   *
   * @param id 试卷ID
   */
  void deletePaperById(Integer id);

  /**
   * 通过专业ID查询试卷 List 集合
   *
   * @param majorId 专业ID
   * @return 试卷 List 集合
   */
  List<Paper> selectByMajorId(Integer majorId);

  /**
   * 更新试卷题目ID
   *
   * @param dto 修改的信息
   */
  void updateQuestionId(PaperQuestionUpdateDto dto);

  /**
   * 通过教师 ID 查询所有类型试卷
   *
   * @param teacherId 教师ID
   * @return 试卷 List 集合
   */
  List<Paper> listByTeacherId(Integer teacherId);

  /**
   * 导入试卷局部随机
   *
   * @param paper  试卷参数
   * @param entity 局部随机数据传输对象
   */
  void saveWithImportPaper(Paper paper, ImportPaperRandomQuestionDto entity);

  /**
   * 通过试卷模板 ID 查询试卷数量
   *
   * @param paperFormId 试卷模板ID
   * @return 数量
   */
  int countPaperByPaperFormId(Integer paperFormId);

  /**
   * 分页查询试卷信息
   *
   * @param page   分页信息
   * @param entity 模糊搜索条件
   * @return 分页结果集
   */
  Map<String, Object> pagePaper(Page<Paper> page, QueryPaperDto entity);

  /**
   * 根据教师id查找未开始考试的试卷
   *
   * @param page 分页信息
   * @return 试卷信息
   */
  Map<String, Object> pageUndoPaper(Page<Paper> page, QueryPaperDto entity);
}
