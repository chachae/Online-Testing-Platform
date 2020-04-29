package com.chachae.exam.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chachae.exam.common.dao.MajorDAO;
import com.chachae.exam.common.exception.ServiceException;
import com.chachae.exam.common.model.Major;
import com.chachae.exam.common.model.Paper;
import com.chachae.exam.common.util.PageUtil;
import com.chachae.exam.service.MajorService;
import com.chachae.exam.service.PaperService;
import com.chachae.exam.service.StudentService;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 专业表服务实现类
 *
 * @author chachae
 * @since 2020-02-08 14:26:53
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class MajorServiceImpl extends ServiceImpl<MajorDAO, Major> implements MajorService {

  @Resource
  private MajorDAO majorDAO;
  @Resource
  private PaperService paperService;
  @Resource
  private StudentService studentService;

  @Override
  public Map<String, Object> listPage(Page<Major> page, Major major) {
    return PageUtil.toPage(this.majorDAO.pageVo(page, major));
  }

  @Override
  public List<Major> listByAcademyId(Integer academyId) {
    LambdaQueryWrapper<Major> qw = new LambdaQueryWrapper<>();
    qw.eq(Major::getAcademyId, academyId);
    return this.majorDAO.selectList(qw);
  }

  @Override
  public List<Major> listByMajorName(String majorName) {
    LambdaQueryWrapper<Major> qw = new LambdaQueryWrapper<>();
    qw.eq(Major::getMajor, majorName);
    return this.majorDAO.selectList(qw);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean removeById(Serializable id) {
    List<Paper> papers = this.paperService.selectByMajorId((int) id);
    if (CollUtil.isNotEmpty(papers)) {
      throw new ServiceException("专业存在试卷关联，请删除试卷后重试");
    }
    int studentCount = this.studentService.selectCountByMajorId((int) id);
    if (studentCount > 0) {
      throw new ServiceException("专业存在学生关联，请删除相关学生后重试");
    }
    return super.removeById(id);
  }

  @Override
  public boolean save(Major entity) {
    if (CollUtil.isEmpty(this.listByMajorName(entity.getMajor()))) {
      return super.save(entity);
    } else {
      throw new ServiceException("专业已存在，请重重新输入");
    }
  }

  @Override
  public boolean updateById(Major entity) {
    if (CollUtil.isEmpty(this.listByMajorName(entity.getMajor()))) {
      return super.updateById(entity);
    } else {
      throw new ServiceException("专业已存在，请重重新输入");
    }
  }
}
