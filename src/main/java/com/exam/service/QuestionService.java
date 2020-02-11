package com.exam.service;

import com.exam.entity.Course;
import com.exam.entity.Question;
import com.exam.entity.StuAnswerRecord;
import com.exam.entity.Type;
import com.exam.entity.dto.ImportPaperDto;
import com.exam.entity.dto.StudentAnswerDto;
import com.github.pagehelper.PageInfo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 试题接口
 *
 * @author yzn
 * @date 2020/1/22
 */
public interface QuestionService {

  /**
   * 显示试题库并分页
   *
   * @param pageNo 当前页
   * @return 分页信息结果集
   */
  PageInfo<Question> pageForQuestionList(Integer pageNo);

  /**
   * 通过ID 查询问题的信息
   *
   * @param id 问题的 ID
   * @return 问题信息
   */
  Question findById(Integer id);

  /**
   * 通过 ID 查找课程
   *
   * @param courseId 课程ID
   * @return 课程信息
   */
  Course findByCourseId(Integer courseId);

  /**
   * 查找所有的试题类型
   *
   * @return 实体类型集合
   */
  List<Type> findAllType();

  /**
   * 根据教师id查找他教的所有课程（可以出题的课程）
   *
   * @param teacherId 教师ID
   * @return 课程集合
   */
  List<Course> findTeacherCourse(Integer teacherId);

  /**
   * 新增试题
   *
   * @param question 试题信息
   */
  void saveNewQuestion(Question question);

  /**
   * 修改试题
   *
   * @param id 试题ID
   * @param questionName 问题名称
   * @param answer 问题答案
   * @param remark 问题解析
   */
  void editQuestion(Integer id, String questionName, String answer, String remark);

  /**
   * 通过 ID 删除试题
   *
   * @param id 试题ID
   */
  void deleteQuestion(Integer id);

  /**
   * 根据课程id和题目类型获取所有该类型的题目id集合
   *
   * @param typeId 类型ID
   * @return ID集合
   */
  List<Integer> getIdList(Integer typeId, Integer courseId);

  /**
   * 根据答案记录集合查找试题集合
   *
   * @param answerRecordList 答题记录
   * @return 问题正确答案
   */
  List<Question> findByAnswerRecordList(List<StuAnswerRecord> answerRecordList);

  /**
   * 整理出学生答案 以Map<问题 ID，<问题、答案>>的形式
   *
   * @param answerRecordList 学生的主观题答案
   * @param questionList 题库中的主观题标准答案
   * @return Map 键值信息
   */
  List<StudentAnswerDto> findMapByStuAnswerRecordAndQuestionList(
      List<StuAnswerRecord> answerRecordList, List<Question> questionList);

  /**
   * 导入试卷问题
   *
   * @param multipartFile multipartFile 对象
   * @return 考试id
   */
  ImportPaperDto importQuestion(MultipartFile multipartFile);
}
