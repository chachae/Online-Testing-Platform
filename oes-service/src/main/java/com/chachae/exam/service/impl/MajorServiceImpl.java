package com.chachae.exam.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chachae.exam.common.model.Major;
import com.chachae.exam.common.model.Paper;
import com.chachae.exam.common.model.vo.MajorVo;
import com.chachae.exam.common.exception.ServiceException;
import com.chachae.exam.common.dao.MajorDAO;
import com.chachae.exam.service.MajorService;
import com.chachae.exam.service.PaperService;
import com.chachae.exam.service.StudentService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;

/**
 * 专业表服务实现类
 *
 * @author chachae
 * @since 2020-02-08 14:26:53
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class MajorServiceImpl extends ServiceImpl<MajorDAO, Major> implements MajorService {

  @Resource private MajorDAO majorDAO;
  @Resource private PaperService paperService;
  @Resource private StudentService studentService;

  @Override
  public PageInfo<MajorVo> pageForMajorList(Integer pageNo, Major major) {
    // 设置分页信息，默认每页显示 12 条数据，此处采用 PageHelper 物理分页插件实现数据分页
    PageHelper.startPage(pageNo, 12);
    List<MajorVo> majors = this.majorDAO.listVo(major);
    return new PageInfo<>(majors);
  }

  @Override
  public List<Major> listByAcademyId(Integer academyId) {
    QueryWrapper<Major> qw = new QueryWrapper<>();
    qw.lambda().eq(Major::getAcademyId, academyId);
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
}