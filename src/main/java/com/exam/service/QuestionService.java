package com.exam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.exam.entity.Course;
import com.exam.entity.Question;
import com.exam.entity.StuAnswerRecord;
import com.exam.entity.dto.ImportPaperDto;
import com.exam.entity.dto.StudentAnswerDto;
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
   * @return 分页信息结果集
   */
  PageInfo<Question> pageForQuestionList(Integer pageNo, Integer courseId);

  /**
   * 根据paperId和试题类型查找该类型题目集合
   *
   * @param paperId 试卷ID
   * @param qChoiceType 试卷类型
   * @return 问题 Set 集合
   */
  Set<Question> selectByPaperIdAndType(Integer paperId, Integer qChoiceType);

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
   * 根据答案记录集合查找试题集合
   *
   * @param answerRecordList 答题记录
   * @return 问题正确答案
   */
  List<Question> listByAnswerRecordList(List<StuAnswerRecord> answerRecordList);

  /**
   * 整理出学生答案 以Map<问题 ID，<问题、答案>>的形式
   *
   * @param answerRecordList 学生的主观题答案
   * @param questionList 题库中的主观题标准答案
   * @return Map 键值信息
   */
  List<StudentAnswerDto> listMapByStuAnswerRecordAndQuestionList(
      List<StuAnswerRecord> answerRecordList, List<Question> questionList);

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
   * @param courseId 课程id
   * @return 题目列表
   */
  List<Question> listByQuestionNameAndCourseId(String questionName, Integer courseId);
}
