package com.chachae.exam.common.constant;

/**
 * 系统常量池
 *
 * @author chachae
 * @since 2020/2/6 17:33
 */
public class SysConsts {

  /**
   * 默认密码
   */
  public static final String DEFAULT_PASSWORD = "123456";

  /**
   * 试卷状态常量
   */
  public static class Paper {

    public static final String PAPER_TYPE_PRACTICE = "模拟";
    public static final String PAPER_TYPE_FORMAL = "正式";
    public static final String PAPER_STATE_START = "未开始";
    public static final String PAPER_STATE_END = "已结束";
  }

  /**
   * Session 常量
   */
  public static class Session {

    // 教師ID
    public static final String TEACHER_ID = "teacherId";
    // 管理員ID
    public static final String ROLE_ID = "roleId";
    // 学生ID
    public static final String STUDENT_ID = "studentId";
    // 教师信息session
    public static final String TEACHER = "teacher";
    // 学生信息session
    public static final String STUDENT = "student";
    // 管理员信息session
    public static final String ADMIN = "admin";
  }

  /**
   * 角色代码
   */
  public static class Role {

    /**
     * 管理员
     */
    public static final Integer ADMIN = 1;
    /**
     * 学生
     */
    public static final Integer STUDENT = 2;
    /**
     * 教师
     */
    public static final Integer TEACHER = 3;
  }

  /**
   * 试题难度
   */
  public static class Diff {

    public static final String AVG = "0";
    public static final String SIMPLE = "1";
    public static final String MID = "2";
    public static final String HARD = "3";
  }

  /**
   * 试卷模板类型
   */
  public static class PaperForm {

    /**
     * 导入试卷自增
     */
    public static final Integer IMPORT = 1;
    /**
     * 页面添加
     */
    public static final Integer INSERT = 0;
  }
}
