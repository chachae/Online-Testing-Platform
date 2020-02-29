package com.exam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.exam.entity.Course;
import com.exam.entity.Question;
import com.exam.entity.dto.ImportPaperDto;
import com.exam.entity.dto.StuAnswerRecordDto;
import com.exam.entity.vo.QuestionVo;
import com.github.pagehelper.PageInfo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

/**
 * 试题接口
 *
 * @author yzn
 * @date 2020/1/22
 */
public interface QuestionService extends IService<Question> {

  /**
   * 显示试题库并分页
   *
   * @param pageNo 当前页
   * @param courseId 课程ID
   * @param typeId 类型ID
   * @return 分页信息结果集
   */
  PageInfo<Question> pageForQuestionList(Integer pageNo, Integer courseId, Integer typeId);

  /**
   * 根据paperId和试题类型查找该类型题目集合
   *
   * @param paperId 试卷ID
   * @param typeId 试卷类型
   * @return 问题 Set 集合
   */
  Set<Question> selectByPaperIdAndType(Integer paperId, Integer typeId);

  /**
   * 根据教师id查找他教的所有课程（可以出题的课程）
   *
   * @param teacherId 教师ID
   * @return 课程集合
   */
  List<Course> selectCourseByTeacherId(Integer teacherId);

  /**
   * 根据教师id查找他教的所有课程ID（可以出题的课程）
   *
   * @return 课程 ID 集合
   */
  List<Integer> selectIdsFilterByTeacherId();

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
