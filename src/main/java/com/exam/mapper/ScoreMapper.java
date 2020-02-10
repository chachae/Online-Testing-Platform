package com.exam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.exam.entity.Score;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 分数 Mapper 接口
 *
 * @author yzn
 * @date 2020/1/3
 */
public interface ScoreMapper extends BaseMapper<Score> {

  /**
   * 统计平均分
   *
   * @param paperId 试卷ID
   * @return 平均分
   */
  @Select("SELECT AVG(score) AS avg FROM score WHERE paper_id = #{paperId}")
  double avgScoreByPaperId(@Param("paperId") Integer paperId);
}
