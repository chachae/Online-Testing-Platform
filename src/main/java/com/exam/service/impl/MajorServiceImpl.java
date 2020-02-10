package com.exam.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exam.entity.Major;
import com.exam.entity.vo.MajorVo;
import com.exam.mapper.MajorMapper;
import com.exam.service.MajorService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * (Major)表服务实现类
 *
 * @author yzn
 * @since 2020-02-08 14:26:53
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class MajorServiceImpl extends ServiceImpl<MajorMapper, Major> implements MajorService {

  @Resource private MajorMapper majorMapper;

  @Override
  public PageInfo<MajorVo> pageForMajorList(Integer pageNo) {
    // 设置分页信息，默认每页显示8条数据，此处采用 PageHelper 物理分页插件实现数据分页
    PageHelper.startPage(pageNo, 8);
    List<MajorVo> majors = this.majorMapper.listVo(null);
    return new PageInfo<>(majors);
  }
}
