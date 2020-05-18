package com.chachae.exam.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chachae.exam.common.dao.GradeDAO;
import com.chachae.exam.common.model.Grade;
import com.chachae.exam.common.model.dto.ImportGradeDto;
import com.chachae.exam.common.model.dto.QueryGradeDto;
import com.chachae.exam.common.model.vo.GradeVo;
import com.chachae.exam.common.util.PageUtil;
import com.chachae.exam.service.GradeService;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author chachae
 * @since 2020/5/18 21:29
 */

@Service
@RequiredArgsConstructor
public class GradeServiceImpl extends ServiceImpl<GradeDAO, Grade> implements GradeService {

  private final GradeDAO gradeDAO;

  @Override
  public List<Grade> listByMajorId(Integer majorId) {
    LambdaQueryWrapper<Grade> qw = new LambdaQueryWrapper<>();
    qw.eq(Grade::getMajorId, majorId);
    return gradeDAO.selectList(qw);
  }

  @Override
  public Map<String, Object> listPage(Page<Grade> page, QueryGradeDto entity) {
    IPage<GradeVo> result = gradeDAO.pageVo(page, entity);
    return PageUtil.toPage(result);
  }

  @Override
  public void save(ImportGradeDto entity) {
    List<Grade> grades = this.listByMajorId(entity.getMajorId());
    int level = entity.getLevel();
    List<Integer> gradeNumbers = grades.stream().filter(grade -> grade.getLevel().equals(level))
        .map(Grade::getGradeNumber).collect(Collectors.toList());
    String[] numbers = StrUtil.splitToArray(entity.getGradeNumbers(), ',');
    Grade grade = new Grade();
    grade.setLevel(entity.getLevel());
    grade.setMajorId(entity.getMajorId());
    for (String number : numbers) {
      int n = Integer.parseInt(number);
      if (!gradeNumbers.contains(n)) {
        grade.setGradeNumber(n);
        this.gradeDAO.insert(grade);
      }
    }
  }
}
