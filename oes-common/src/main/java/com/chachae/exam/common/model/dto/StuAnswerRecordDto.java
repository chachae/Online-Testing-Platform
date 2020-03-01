package com.chachae.exam.common.model.dto;

import com.chachae.exam.common.base.BaseEntity;
import com.chachae.exam.common.model.Score;
import com.chachae.exam.common.model.Student;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 学生主观题答题记录数据传输bean
 *
 * @author chachae
 * @since 2020/2/23 11:41
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class StuAnswerRecordDto extends BaseEntity {

  private Student student;
  private Score score;
  private List<StudentAnswerDto> records;
}
