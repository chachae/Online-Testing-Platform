package com.exam.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exam.entity.Major;
import com.exam.entity.Paper;
import com.exam.entity.vo.MajorVo;
import com.exam.exception.ServiceException;
import com.exam.mapper.MajorMapper;
import com.exam.service.MajorService;
import com.exam.service.PaperService;
import com.exam.service.StudentService;
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
 * @author yzn
 * @since 2020-02-08 14:26:53
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class MajorServiceImpl extends ServiceImpl<MajorMapper, Major> implements MajorService {

  @Resource private MajorMapper majorMapper;
  @Resource private PaperService paperService;
  @Resource private StudentService studentService;

  @Override
  public PageInfo<MajorVo> pageForMajorList(Integer pageNo, Major major) {
    // 设置分页信息，默认每页显示 12 条数据，此处采用 PageHelper 物理分页插件实现数据分页
    PageHelper.startPage(pageNo, 12);
    List<MajorVo> majors = this.majorMapper.listVo(major);
    return new PageInfo<>(majors);
  }

  @Override
  public List<Major> listByAcademyId(Integer academyId) {
    QueryWrapper<Major> qw = new QueryWrapper<>();
    qw.lambda().eq(Major::getAcademyId, academyId);
    return this.majorMapper.selectList(qw);
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
