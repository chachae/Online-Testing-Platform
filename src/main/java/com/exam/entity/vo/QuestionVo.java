package com.exam.entity.vo;

import com.exam.entity.Course;
import com.exam.entity.Question;
import com.exam.entity.Type;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author chachae
 * @since 2020/2/29 21:04
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class QuestionVo extends Question {

  private Course course;

  private Type type;
}
