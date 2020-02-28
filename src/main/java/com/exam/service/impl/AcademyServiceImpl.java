package com.exam.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exam.entity.Academy;
import com.exam.entity.Major;
import com.exam.exception.ServiceException;
import com.exam.mapper.AcademyMapper;
import com.exam.mapper.MajorMapper;
import com.exam.service.AcademyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.Serializable;

/**
 * 学院服务实现类
 *
 * @author chachae
 * @since 2020-02-09 12:09:59
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class AcademyServiceImpl extends ServiceImpl<AcademyMapper, Academy>
    implements AcademyService {

  @Resource private MajorMapper majorMapper;
  @Resource private AcademyMapper academyMapper;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean removeById(Serializable id) {
    // 查询是否有专业于该ID的学院关联，存在的话不允许被删除
    // 这里使用 QueryWrapper 构造器构造查询条件
    QueryWrapper<Major> qw = new QueryWrapper<>();
    qw.lambda().eq(Major::getAcademyId, id);
    // 查询数量，如果为 0 说明不存在关联
    int count = this.majorMapper.selectCount(qw);
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
    if (ObjectUtil.isNotEmpty(this.selectByName(entity.getName()))) {
      throw new ServiceException("学院名称已存在");
    }
    return super.updateById(entity);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean save(Academy entity) {
    // 检测学院名称
    if (ObjectUtil.isNotEmpty(this.selectByName(entity.getName()))) {
      throw new ServiceException("学院名称已存在");
    }
    return super.save(entity);
  }

  @Override
  public Academy selectByName(String academyName) {
    QueryWrapper<Academy> qw = new QueryWrapper<>();
    qw.lambda().eq(Academy::getName, academyName);
    return this.academyMapper.selectOne(qw);
  }
}
