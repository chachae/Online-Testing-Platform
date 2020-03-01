package com.chachae.exam.common.model.vo;

import com.chachae.exam.common.model.Course;
import com.chachae.exam.common.model.Question;
import com.chachae.exam.common.model.Type;
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
