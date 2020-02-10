package com.exam.constant;

/**
 * 系统常量池
 *
 * @author yzn
 * @since 2020/2/6 17:33
 */
public class SysConsts {

  /** 试卷状态常量 */
  public static class PAPER {
    public static final String PAPER_TYPE_PRACTICE = "模拟";
    public static final String PAPER_TYPE_FORMAL = "正式";
    public static final String PAPER_STATE_START = "未开始";
    public static final String PAPER_STATE_END = "已结束";
  }

  /** Session 常量 */
  public static class SESSION {
    public static final String TEACHER_ID = "teacherId";
    public static final String ROLE_ID = "roleId";
    public static final String STUDENT_ID = "studentId";
    public static final String TEACHER = "teacher";
    public static final String STUDENT = "student";
    public static final String ADMIN = "admin";
  }

  /** 试题题型代码 */
  public static class QUESTION {
    /** 单项选择代码 */
    public static final Integer CHOICE_TYPE = 1;
    /** 多项选择代码 */
    public static final Integer MUL_CHOICE_TYPE = 2;
    /** 判断题代码 */
    public static final Integer TOF_TYPE = 3;
    /** 填空题代码 */
    public static final Integer FILL_TYPE = 4;
    /** 主观题代码 */
    public static final Integer SAQ_TYPE = 5;
    /** 编程题代码 */
    public static final Integer PROGRAM_TYPE = 6;
  }

  /** 角色代码 */
  public static class ROLE {
    /** 管理员 */
    public static final Integer ADMIN = 1;
    /** 学生 */
    public static final Integer STUDENT = 2;
    /** 教师 */
    public static final Integer TEACHER = 3;
  }
}
