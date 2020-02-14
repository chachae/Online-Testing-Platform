package com.exam.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exam.entity.Announce;
import com.exam.mapper.AnnounceMapper;
import com.exam.service.AnnounceService;
import com.exam.util.DateUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.stream.Stream;

/**
 * 公告业务实现类
 *
 * @author yzn
 * @since 2020/2/14 17:42
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class AnnounceServiceImpl extends ServiceImpl<AnnounceMapper, Announce>
    implements AnnounceService {

  @Resource private AnnounceMapper announceMapper;

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
    // 使用 lambda 表达式循环删除数据
    Stream.of(ids).forEach(id -> announceMapper.deleteById((id)));
  }
}
