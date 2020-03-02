package com.chachae.exam.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.chachae.exam.common.model.Announce;
import com.chachae.exam.common.dao.AnnounceDAO;
import com.chachae.exam.common.util.DateUtil;
import com.chachae.exam.service.AnnounceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 公告业务实现类
 *
 * @author chachae
 * @since 2020/2/14 17:42
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class AnnounceServiceImpl extends ServiceImpl<AnnounceDAO, Announce>
    implements AnnounceService {

  @Resource private AnnounceDAO announceDAO;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean save(Announce entity) {
    // 封装时间创建时间参数
    entity.setCreateTime(DateUtil.getDate());
    return super.save(entity);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void delete(Integer[] ids) {
    // 遍历 ID 删除数据
    for (Integer id : ids) {
      this.announceDAO.deleteById(id);
    }
  }
}