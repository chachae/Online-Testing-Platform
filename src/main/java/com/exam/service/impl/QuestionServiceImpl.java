package com.exam.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exam.constant.SysConsts;
import com.exam.entity.Course;
import com.exam.entity.Paper;
import com.exam.entity.PaperForm;
import com.exam.entity.Question;
import com.exam.entity.dto.ImportPaperDto;
import com.exam.entity.dto.QuestionDto;
import com.exam.entity.dto.StuAnswerRecordDto;
import com.exam.entity.dto.StudentAnswerDto;
import com.exam.exception.ServiceException;
import com.exam.mapper.CourseMapper;
import com.exam.mapper.PaperFormMapper;
import com.exam.mapper.PaperMapper;
import com.exam.mapper.QuestionMapper;
import com.exam.service.CourseService;
import com.exam.service.QuestionService;
import com.exam.util.BeanUtil;
import com.exam.util.FileUtil;
import com.exam.util.HttpContextUtil;
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
import java.util.*;

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
  @Resource private CourseService courseService;

  @Override
  public PageInfo<Question> pageForQuestionList(Integer pageNo, Integer courseId, Integer typeId) {
    // 获取本人的课程id
    List<Integer> ids = this.selectIdsFilterByTeacherId();
    QueryWrapper<Question> qw = new QueryWrapper<>();
    qw.lambda().in(Question::getCourseId, ids);
    // 条件情况判断
    if (courseId != null) {
      qw.lambda().eq(Question::getCourseId, courseId);
    }
    if (typeId != null) {
      qw.lambda().eq(Question::getTypeId, typeId);
    }
    // 设置分页信息，默认每页显示12条数据，此处采用 PageHelper 物理分页插件实现数据分页
    PageHelper.startPage(pageNo, 12);
    // 查询试题集合信息
    // 只查询本人的试题
    // 过滤课程
    List<Question> questionList = questionMapper.selectList(qw);
    return new PageInfo<>(questionList);
  }

  @Override
  public Set<Question> selectByPaperIdAndType(Integer paperId, Integer qChoiceType) {
    // 通过 ID 查询试卷信息
    Paper paper = this.paperMapper.selectById(paperId);
    // 获取试卷的题目序号集合，Example:（1,2,3,4,5,6,7）
    String qIds = paper.getQuestionId();
    // 分割题目序号
    List<String> ids = StrUtil.split(qIds, StrUtil.C_COMMA);
    // 实现随机排序的核心方法
    Collections.shuffle(ids);
    Set<Question> questionSet = Sets.newLinkedHashSet();
    for (String id : ids) {
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
  public List<Integer> selectIdsFilterByTeacherId() {
    // 获取 session
    Integer id = (Integer) HttpContextUtil.getSession().getAttribute(SysConsts.SESSION.TEACHER_ID);
    List<Course> courses = this.courseService.listByTeacherId(id);
    List<Integer> ids = Lists.newArrayList();
    courses.forEach(c -> ids.add(c.getId()));
    return ids;
  }

  @Override
  public List<Question> listByTypeIdAndCourseId(Integer typeId, Integer courseId) {
    // 使用 QueryWrapper 条件构造器构造 Sql 条件
    QueryWrapper<Question> qw = new QueryWrapper<>();
    qw.lambda().eq(Question::getTypeId, typeId).eq(Question::getCourseId, courseId);
    // 获取所有对应条件的问题集合
    return questionMapper.selectList(qw);
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
      for (String e : ids) {
        // 比较 ID 值
        if (Integer.parseInt(e) == id) {
          throw new ServiceException("试题被试卷[ " + paper.getPaperName() + " ]关联，不允许删除");
        }
      }
    }
    // 至此，不存在，执行删除
    this.removeById(id);
  }

  @Override
  public List<Question> listByQuestionNameAndCourseIdAndTypeId(
      String questionName, Integer courseId, Integer typeId) {
    QueryWrapper<Question> qw = new QueryWrapper<>();
    // 构造查询条件
    qw.lambda().eq(Question::getQuestionName, questionName);
    qw.lambda().eq(Question::getCourseId, courseId);
    qw.lambda().eq(Question::getTypeId, typeId);
    return this.questionMapper.selectList(qw);
  }

  @Override
  public List<Question> listByStuAnswerRecordDto(StuAnswerRecordDto entity) {
    // 获取记录对象集合
    List<StudentAnswerDto> records = entity.getRecords();
    List<Question> result = Lists.newArrayList();
    // 循环集合
    records.forEach(record -> result.add(this.questionMapper.selectById(record.getQuestionId())));
    // 根据问题的 ID 排序
    result.sort(Comparator.comparingInt(Question::getId));
    return result;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public ImportPaperDto importPaper(MultipartFile multipartFile) {
    try {
      // 准备一个 Map 用来存储题目的类型和数量
      Map<Integer, Integer> typeNumMap = Maps.newHashMap();
      // 初始化题型数量数据
      for (int i = 1; i <= 6; i++) {
        typeNumMap.put(i, 0);
      }
      // 准备一个 Map 用来存储题目的类型和分值
      Map<Integer, Integer> typeScoreMap = Maps.newHashMap();
      // 初始化题型分值数据
      for (int i = 1; i <= 6; i++) {
        typeScoreMap.put(i, 0);
      }

      // 考试名称
      String paperName = FileUtil.getFileNameNoEx(multipartFile.getOriginalFilename());
      File file = FileUtil.toFile(multipartFile);
      // 读取 Excel 中的数据
      ExcelReader reader = ExcelUtil.getReader(file);
      // 读取问题的信息
      List<QuestionDto> questions = reader.readAll(QuestionDto.class);

      // 插入问题表并组装ID
      List<Integer> idList = Lists.newArrayList();
      for (QuestionDto question : questions) {
        // 判断是否存在同名、同课程、同类型题目
        String qName = question.getQuestionName();
        Integer cid = question.getCourseId();
        Integer typeId = question.getTypeId();
        List<Question> theSames = this.listByQuestionNameAndCourseIdAndTypeId(qName, cid, typeId);
        // 集合不为空，说明存在同名同课程题目
        if (CollUtil.isNotEmpty(theSames)) {
          // 取出第一条数据
          Question sameQuestion = theSames.get(0);
          // 获取数据的id并组装到 idList 中
          idList.add(sameQuestion.getId());
        } else {
          // 可直接插入
          // 复制 QuestionDto 的数据到 Question 中
          Question res = BeanUtil.copyObject(question, Question.class);
          // 向数据库插入数据
          this.questionMapper.insert(res);
          // 获取数据的id并组装到 idList 中
          idList.add(res.getId());
        }

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

      // 设置模板类型（1）
      form.setType(SysConsts.PAPER_FORM.IMPORT);

      // 插入数据
      this.paperFormMapper.insert(form);
      // 模板ID
      Integer formId = form.getId();
      // 建立 ImportPaperDto 对象
      ImportPaperDto dto = new ImportPaperDto();
      // 建立 StringBuilder 对象，用户组装试题集合
      StringBuilder sb = new StringBuilder();
      // 拼接试题 ID 和 逗号
      idList.forEach(id -> sb.append(id).append(StrUtil.COMMA));
      String ids = sb.toString();
      // 去除最后一个逗号并封装题序参数
      dto.setQuestionIdList(ids.substring(0, ids.length() - 1));

      // 计算试卷卷面分
      int score =
          typeNumMap.get(1) * typeScoreMap.get(1)
              + typeNumMap.get(2) * typeScoreMap.get(2)
              + typeNumMap.get(3) * typeScoreMap.get(3)
              + typeNumMap.get(4) * typeScoreMap.get(4)
              + typeNumMap.get(5) * typeScoreMap.get(5)
              + typeNumMap.get(6) * typeScoreMap.get(6);

      // 封装问题信息参数
      dto.setPaperName(paperName);
      dto.setPaperFormId(formId);
      dto.setScore(score);
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
      List<Integer> ids = this.selectIdsFilterByTeacherId();
      for (Question question : questions) {
        // 正确答案、难度、所属课程、类型 ID 检测
        boolean isTypeIdNull = question.getTypeId() == null;
        boolean isCourseIdNull = question.getCourseId() == null;
        boolean isAnsIdNull = question.getDifficulty() == null;
        boolean isDefIdNull = question.getAnswer() == null;
        if (isTypeIdNull || isCourseIdNull || isAnsIdNull || isDefIdNull) {
          throw new ServiceException();
        }
        // 过滤同名、同课程、同类型题目
        // 题目名称
        String questionName = question.getQuestionName();
        // 题目课程 id
        Integer courseId = question.getCourseId();
        // 类型 id
        Integer typeId = question.getTypeId();
        List<Question> result =
            this.listByQuestionNameAndCourseIdAndTypeId(questionName, courseId, typeId);
        if (CollUtil.isEmpty(result)) {
          // 如果教师包含该课程，则允许插入
          if (ids.contains(courseId)) {
            // 插入题目数据
            this.questionMapper.insert(question);
          }
        }
      }
    } catch (ServiceException e) {
      throw new ServiceException("请检查试题中是否漏填如下信息（课程ID、正确答案、难度、类型）");
    } catch (Exception e) {
      // 捕捉所有可能发生的异常，抛出给控制层处理
      throw new ServiceException("题目解析失败");
    }
  }
}
