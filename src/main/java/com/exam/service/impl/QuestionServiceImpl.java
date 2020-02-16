package com.exam.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exam.constant.SysConsts;
import com.exam.entity.*;
import com.exam.entity.dto.ImportPaperDto;
import com.exam.entity.dto.QuestionDto;
import com.exam.entity.dto.StudentAnswerDto;
import com.exam.exception.ServiceException;
import com.exam.mapper.CourseMapper;
import com.exam.mapper.PaperFormMapper;
import com.exam.mapper.PaperMapper;
import com.exam.mapper.QuestionMapper;
import com.exam.service.QuestionService;
import com.exam.util.BeanUtil;
import com.exam.util.FileUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 试题业务实现
 *
 * @author yzn
 * @date 2020/2/1
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question>
    implements QuestionService {

  @Resource private QuestionMapper questionMapper;
  @Resource private CourseMapper courseMapper;
  @Resource private PaperMapper paperMapper;
  @Resource private PaperFormMapper paperFormMapper;

  @Override
  public PageInfo<Question> pageForQuestionList(Integer pageNo) {
    // 设置分页信息，默认每页显示8条数据，此处采用 PageHelper 物理分页插件实现数据分页
    PageHelper.startPage(pageNo, 10);
    // 查询试题集合信息
    List<Question> questionList = questionMapper.selectList(null);
    return new PageInfo<>(questionList);
  }

  @Override
  public Set<Question> selectByPaperIdAndType(Integer paperId, Integer qChoiceType) {
    // 通过 ID 查询试卷信息
    Paper paper = this.paperMapper.selectById(paperId);
    // 获取试卷的题目序号集合，Example:（1,2,3,4,5,6,7）
    String qIds = paper.getQuestionId();
    // 分割题目序号
    String[] qIdArray = StrUtil.splitToArray(qIds, StrUtil.C_COMMA);
    Set<Question> questionSet = Sets.newHashSet();
    for (String id : qIdArray) {
      // 通过题目 ID 获取问题的信息
      Question question = questionMapper.selectById(id);
      if (qChoiceType.equals(question.getTypeId())) {
        questionSet.add(question);
      }
    }
    return questionSet;
  }

  @Override
  public List<Course> selectCourseByTeacherId(Integer teacherId) {
    QueryWrapper<Course> qw = new QueryWrapper<>();
    qw.lambda().eq(Course::getTeacherId, teacherId);
    return this.courseMapper.selectList(qw);
  }

  @Override
  public List<Question> listByTypeIdAndCourseId(Integer typeId, Integer courseId) {
    // 使用 QueryWrapper 条件构造器构造 Sql 条件
    QueryWrapper<Question> qw = new QueryWrapper<>();
    qw.lambda().eq(Question::getTypeId, typeId).eq(Question::getCourseId, courseId);
    // 获取所有对饮条件的问题集合
    return questionMapper.selectList(qw);
  }

  @Override
  public List<Question> listByAnswerRecordList(List<StuAnswerRecord> answerRecordList) {
    if (CollUtil.isEmpty(answerRecordList)) {
      throw new ServiceException("未找到主观题答案记录！");
    }
    // 获取当前所有ID主观题的正确答案并返回结果集合
    List<Question> questionList = Lists.newArrayList();
    for (StuAnswerRecord answerRecord : answerRecordList) {
      // 调用问题查询接口
      Question question = questionMapper.selectById(answerRecord.getQuestionId());
      questionList.add(question);
    }
    return questionList;
  }

  @Override
  public List<StudentAnswerDto> listMapByStuAnswerRecordAndQuestionList(
      List<StuAnswerRecord> answerRecordList, List<Question> questionList) {
    List<StudentAnswerDto> res = Lists.newArrayList();
    // 循环问题集合
    for (Question question : questionList) {
      // 循环学生的答题记录集合
      for (StuAnswerRecord s : answerRecordList) {
        // 比较问题的 ID 是否和学生答题记录所属 ID 相同
        if (question.getId().equals(s.getQuestionId())) {
          // 将学生答题记录的问题题目和回答内容组装发到
          StudentAnswerDto dto = new StudentAnswerDto();
          dto.setId(s.getId());
          dto.setQuestionName(question.getQuestionName());
          dto.setAnswer(s.getAnswer());
          dto.setScore(s.getScore());
          res.add(dto);
        }
      }
    }
    return res;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deleteById(Integer id) {
    List<Paper> papers = this.paperMapper.selectList(null);
    // 查询是否有试卷与试题关联
    for (Paper paper : papers) {
      // 获取试卷的题目 ID，并且转数组
      String questionId = paper.getQuestionId();
      String[] ids = StrUtil.splitToArray(questionId, StrUtil.C_COMMA);
      // 循环题目 ID
      for (String s : ids) {
        // 比较 ID 值
        if (Integer.parseInt(s) == id) {
          throw new ServiceException("试题被试卷[ " + paper.getPaperName() + " ]关联，不允许删除");
        }
      }
    }
    // 至此，不存在，执行删除
    this.removeById(id);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public ImportPaperDto importPaper(MultipartFile multipartFile) {
    try {
      // 准备一个 Map 用来存储题目的类型和数量
      Map<Integer, Integer> typeNumMap = Maps.newHashMap();
      initMap(typeNumMap);
      // 准备一个 Map 用来存储题目的类型和分值
      Map<Integer, Integer> typeScoreMap = Maps.newHashMap();
      initMap(typeScoreMap);
      // 考试名称
      String paperName = FileUtil.getFileNameNoEx(multipartFile.getOriginalFilename());
      File file = FileUtil.toFile(multipartFile);
      ExcelReader reader = ExcelUtil.getReader(file);
      // 读取问题的信息
      List<QuestionDto> questions = reader.readAll(QuestionDto.class);
      // 插入问题表并组装ID
      List<Integer> idList = Lists.newArrayList();
      for (QuestionDto question : questions) {
        // 复制 QuestionDto 的数据到 Question 中
        Question res = BeanUtil.copyObject(question, Question.class);
        // 向数据库插入数据
        this.questionMapper.insert(res);
        // 获取数据的id并组装到 idList 中
        idList.add(res.getId());
        // 获取问题的类型
        Integer typeId = question.getTypeId();
        // 计算该类型问题的数量和每道题的分值
        Integer num = typeNumMap.get(typeId);
        num++;
        // 存储数量
        typeNumMap.put(typeId, num);
        // 存储分值
        typeScoreMap.put(typeId, question.getScore());
      }

      // 插入试卷模板信息
      PaperForm form = new PaperForm();
      // 设置数量分布
      form.setQChoiceNum(String.valueOf(typeNumMap.get(SysConsts.QUESTION.CHOICE_TYPE)));
      form.setQMulChoiceNum(String.valueOf(typeNumMap.get(SysConsts.QUESTION.MUL_CHOICE_TYPE)));
      form.setQTofNum(String.valueOf(typeNumMap.get(SysConsts.QUESTION.TOF_TYPE)));
      form.setQFillNum(String.valueOf(typeNumMap.get(SysConsts.QUESTION.FILL_TYPE)));
      form.setQSaqNum(String.valueOf(typeNumMap.get(SysConsts.QUESTION.SAQ_TYPE)));
      form.setQProgramNum(String.valueOf(typeNumMap.get(SysConsts.QUESTION.PROGRAM_TYPE)));
      // 从选择一直到编程题6种题型，设置每到分值
      form.setQChoiceScore(String.valueOf(typeScoreMap.get(SysConsts.QUESTION.CHOICE_TYPE)));
      form.setQMulChoiceScore(String.valueOf(typeScoreMap.get(SysConsts.QUESTION.MUL_CHOICE_TYPE)));
      form.setQTofScore(String.valueOf(typeScoreMap.get(SysConsts.QUESTION.TOF_TYPE)));
      form.setQFillScore(String.valueOf(typeScoreMap.get(SysConsts.QUESTION.FILL_TYPE)));
      form.setQSaqScore(String.valueOf(typeScoreMap.get(SysConsts.QUESTION.SAQ_TYPE)));
      form.setQProgramScore(String.valueOf(typeScoreMap.get(SysConsts.QUESTION.PROGRAM_TYPE)));
      this.paperFormMapper.insert(form);
      // 模板ID
      Integer formId = form.getId();
      // 建立 ImportPaperDto 对象
      ImportPaperDto dto = new ImportPaperDto();
      // 建立 StringBuilder 对象，用户组装试题集合
      StringBuilder sb = new StringBuilder();
      // 拼接试题 ID 和 逗号
      for (Integer id : idList) {
        String idStr = String.valueOf(id);
        sb.append(idStr);
        sb.append(StrUtil.COMMA);
      }
      String ids = sb.toString();
      // 去除最后一个逗号并封装题序参数
      dto.setQuestionIdList(ids.substring(0, ids.length() - 1));
      // 封装问题信息参数
      dto.setPaperName(paperName);
      dto.setPaperFormId(formId);
      return dto;
    } catch (Exception e) {
      throw new ServiceException("试题解析失败");
    }
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void importQuestion(MultipartFile multipartFile) {
    try {
      // 将 multipartFile 转 File
      File file = FileUtil.toFile(multipartFile);
      // 使用 ExcelUtil 读取 Excel 中的数据
      ExcelReader reader = ExcelUtil.getReader(file);
      // 输出到 Question 的 List 集合中
      List<Question> questions = reader.readAll(Question.class);
      // 通过 lambda 循环的方式将题目数据一次插入 Question 表中
      questions.forEach(q -> this.questionMapper.insert(q));
    } catch (Exception e) {
      // 捕捉所有可能发生的异常，抛出给控制层处理
      throw new ServiceException("题目解析失败");
    }
  }

  /**
   * 初始化Map
   *
   * @param map Map 对象
   */
  private void initMap(Map<Integer, Integer> map) {
    map.put(1, 0);
    map.put(2, 0);
    map.put(3, 0);
    map.put(4, 0);
    map.put(5, 0);
    map.put(6, 0);
  }
}
