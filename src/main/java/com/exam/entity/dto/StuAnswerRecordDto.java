package com.exam.entity.dto;

import com.exam.common.BaseEntity;
import com.exam.entity.Score;
import com.exam.entity.Student;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 学生主观题答题记录数据传输bean
 *
 * @author yzn
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
