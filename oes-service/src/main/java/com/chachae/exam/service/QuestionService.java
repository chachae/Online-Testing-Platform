package com.chachae.exam.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chachae.exam.common.model.Question;
import com.chachae.exam.common.model.dto.ImportPaperDto;
import com.chachae.exam.common.model.dto.QuestionQueryDto;
import com.chachae.exam.common.model.dto.StuAnswerRecordDto;
import com.chachae.exam.common.model.vo.QuestionVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 试题接口
 *
 * @author chachae
 * @date 2020/1/22
 */
public interface QuestionService extends IService<Question> {

  /**
   * 显示试题库并分页
   *
   * @param page 分页信息
   * @param entity 模糊条件
   * @return 分页信息结果集
   */
  Map<String, Object> listPage(Page<Question> page, QuestionQueryDto entity);

  /**
   * 根据paperId和试题类型查找该类型题目集合
   *
   * @param paperId 试卷ID
   * @param typeId 试卷类型
   * @return 问题 List 集合
   */
  List<Question> selectByPaperIdAndType(Integer paperId, Integer typeId);

  /**
   * 通过题目类型 ID 和课程 ID 获取问题 List 集合
   *
   * @param typeId 题目类型ID
   * @param courseId 课程ID
   * @return 问题集合
   */
  List<Question> listByTypeIdAndCourseId(Integer typeId, Integer courseId);

  /**
   * 导入试卷问题
   *
   * @param multipartFile multipartFile 对象
   * @return 考试id
   */
  ImportPaperDto importPaper(MultipartFile multipartFile);

  /**
   * 导入题目
   *
   * @param multipartFile multipartFile 对象
   */
  void importQuestion(MultipartFile multipartFile);

  /**
   * 通过 ID 删除试题
   *
   * @param id 试题ID
   */
  void deleteById(Integer id);

  /**
   * 通过题目名称查询题目列表
   *
   * @param questionName 题目名称
   * @param courseId 课程ID
   * @param typeId 题目类型ID
   * @return 题目列表
   */
  List<Question> listByQuestionNameAndCourseIdAndTypeId(
      String questionName, Integer courseId, Integer typeId);

  /**
   * 通过试卷答题记录查询问题的正确答案集合
   *
   * @param entity 答题记录数据传输对象
   * @return 题目集合
   */
  List<Question> listByStuAnswerRecordDto(StuAnswerRecordDto entity);

  /**
   * 通过 ID 查询试题的 VO 信息
   *
   * @param id 试题 ID
   * @return 试题 VO 信息
   */
  QuestionVo selectVoById(Integer id);
}
