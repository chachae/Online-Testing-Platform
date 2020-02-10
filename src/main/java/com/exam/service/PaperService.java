package com.exam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.exam.entity.*;
import com.github.pagehelper.PageInfo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

/**
 * 试卷业务接口
 *
 * @author yzn
 * @date 2020/1/13
 */
public interface PaperService extends IService<Paper> {

  /**
   * 查找该老师所有试卷并分页
   *
   * @param teacherId 教师ID
   * @param pageNo 当前页
   * @return 分页信息结果集
   */
  PageInfo<Paper> pageForPaperList(Integer teacherId, Integer pageNo);

  /**
   * 根据课程 id 查询课程
   *
   * @param courseId 课程ID
   * @return 课程信息
   */
  Course findCourseById(Integer courseId);

  /**
   * 创建一个新的试卷模板
   *
   * @param paperForm 试卷题型信息
   */
  void newPaperForm(PaperForm paperForm);

  /**
   * 查找一个老师所教的所有课程
   *
   * @param teacherId 教师ID
   * @return 课程集合
   */
  List<Course> findCourseListByTeacherId(Integer teacherId);

  /**
   * 组卷
   *
   * @param paper 试卷信息
   */
  void newPaper(Paper paper);

  /**
   * 获取所有试卷模板
   *
   * @return 试卷模板集合
   */
  List<PaperForm> findAllPaperForm();

  /**
   * 根据模版id删除模版
   *
   * @param id 模板ID
   */
  void delPaperFormById(Integer id);

  /**
   * 根据专业班级查找模拟试卷
   *
   * @param majorId 专业ID
   * @return 试卷 List 集合
   */
  List<Paper> findPracticePapersByMajorId(Integer majorId);

  /**
   * 根据paperId和试题类型查找该类型题目集合
   *
   * @param paperId 试卷ID
   * @param qChoiceType 试卷类型
   * @return 问题 Set 集合
   */
  Set<Question> findQuestionsByPaperIdAndType(Integer paperId, Integer qChoiceType);

  /**
   * 教师批改试卷
   *
   * @param stuId 学生ID
   * @param paperId 试卷ID
   * @param request request 对象
   * @return 返回批改后的分数
   */
  String markPaper(Integer stuId, Integer paperId, HttpServletRequest request);

  /**
   * 根据教师id查找未开始考试的试卷
   *
   * @param id 教师ID
   * @return 试卷信息
   */
  List<Paper> findUnDoPaperListByTeacherId(Integer id);

  /**
   * 修改考试时间
   *
   * @param id 考试ID
   * @param paper 试卷信息
   */
  void editPaperById(Integer id, Paper paper);

  /**
   * 根据老师Id查找已结束试卷
   *
   * @param teacherId 教师ID
   * @return 该教师所属已完成的试卷
   */
  List<Paper> findDonePaperListByTeacherId(Integer teacherId);

  /**
   * 根据学生和试卷查找复查试题记录
   *
   * @param stuNumber 学号
   * @param paperId 试卷ID
   * @return 答题信息
   */
  List<StuAnswerRecord> findAnswerRecordByStuAndPaper(String stuNumber, Integer paperId);

  /**
   * 根据专业 ID 获取专业信息
   *
   * @param id 专业ID
   * @return 专业信息
   */
  Major findMajorById(Integer id);

  /**
   * 改变试卷状态未已完成
   *
   * @param id 试卷ID
   */
  void changeStateById(Integer id);

  /**
   * 删除试卷
   *
   * @param id 试卷ID
   */
  void delPaperById(Integer id);

  /**
   * 通过专业ID查询试卷 List 集合
   *
   * @param majorId 专业ID
   * @return 试卷 List 集合
   */
  List<Paper> findPaperByMajorId(Integer majorId);
}
