package com.chachae.exam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chachae.exam.common.dao.AcademyDAO;
import com.chachae.exam.common.dao.MajorDAO;
import com.chachae.exam.common.exception.ServiceException;
import com.chachae.exam.common.model.Academy;
import com.chachae.exam.common.model.Major;
import com.chachae.exam.common.util.PageUtil;
import com.chachae.exam.service.AcademyService;
import java.io.Serializable;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 学院服务实现类
 *
 * @author chachae
 * @since 2020-02-09 12:09:59
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class AcademyServiceImpl extends ServiceImpl<AcademyDAO, Academy> implements AcademyService {

  @Resource
  private MajorDAO majorDAO;
  @Resource
  private AcademyDAO academyDAO;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean removeById(Serializable id) {
    // 查询是否有专业于该ID的学院关联，存在的话不允许被删除
    // 这里使用 QueryWrapper 构造器构造查询条件
    LambdaQueryWrapper<Major> qw = new LambdaQueryWrapper<>();
    qw.eq(Major::getAcademyId, id);
    // 查询数量，如果为 0 说明不存在关联
    int count = this.majorDAO.selectCount(qw);
    if (count > 0) {
      throw new ServiceException("存在专业关联，不允许删除！");
    }
    // 不存在关联，允许删除
    return super.removeById(id);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean updateById(Academy entity) {
    // 检测学院名称
    if (this.selectByName(entity.getName()) != null) {
      throw new ServiceException("学院名称已存在");
    }
    return super.updateById(entity);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean save(Academy entity) {
    // 检测学院名称
    if (this.selectByName(entity.getName()) != null) {
      throw new ServiceException("学院名称已存在");
    }
    return super.save(entity);
  }

  @Override
  public Academy selectByName(String academyName) {
    LambdaQueryWrapper<Academy> qw = new LambdaQueryWrapper<>();
    qw.eq(Academy::getName, academyName);
    return this.academyDAO.selectOne(qw);
  }

  @Override
  public Map<String, Object> listPage(Page<Academy> page) {
    Page<Academy> pageInfo = this.academyDAO.selectPage(page, null);
    return PageUtil.toPage(pageInfo);
  }
}
