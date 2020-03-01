package com.chachae.exam.common.entity.vo;

import com.chachae.exam.common.entity.Course;
import com.chachae.exam.common.entity.Question;
import com.chachae.exam.common.entity.Type;
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
