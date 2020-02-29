package com.exam.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exam.constant.SysConsts;
import com.exam.entity.PaperForm;
import com.exam.exception.ServiceException;
import com.exam.mapper.PaperFormMapper;
import com.exam.service.PaperFormService;
import com.exam.service.PaperService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 试卷模板服务实现类
 *
 * @author yzn
 * @since 2020-02-14 17:51:18
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class PaperFormServiceImpl extends ServiceImpl<PaperFormMapper, PaperForm>
    implements PaperFormService {

  @Resource private PaperService paperService;
  @Resource private PaperFormMapper paperFormMapper;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean removeById(Serializable id) {
    // 调用通过ID查询试卷模板信息接口
    PaperForm form = this.paperFormMapper.selectById(id);
    // 如果 from 对象为空，说明模板不存在
    if (ObjectUtil.isEmpty(form)) {
      throw new ServiceException("试卷模版不存在!");
    }
    // 查找是否有正在使用该模版的试卷，如果有，则不允许删除模版
    int count = this.paperService.countPaperByPaperFormId((int) id);
    // 如果试卷集合对象不为空，说明有试卷正在使用，则不能删除
    if (count > 0) {
      throw new ServiceException("试卷模版正在使用，不能删除该模版！");
    }
    // 至此可以安全删除，调用父级 removeById 方法删除
    return super.removeById(id);
  }

  @Override
  public List<PaperForm> list() {
    List<PaperForm> list = super.list();
    // 过滤出类型1 的数据按模板
    return list.stream()
        // steam 流过滤模板类型
        .filter(e -> e.getType().equals(SysConsts.PAPER_FORM.INSERT))
        // 形成新的 List 集合
        .collect(Collectors.toList());
  }
}
