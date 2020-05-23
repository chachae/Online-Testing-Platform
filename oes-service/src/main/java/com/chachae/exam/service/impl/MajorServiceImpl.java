package com.chachae.exam.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chachae.exam.common.dao.MajorDAO;
import com.chachae.exam.common.exception.ServiceException;
import com.chachae.exam.common.model.Major;
import com.chachae.exam.common.model.Paper;
import com.chachae.exam.common.util.FileUtil;
import com.chachae.exam.common.util.PageUtil;
import com.chachae.exam.service.AcademyService;
import com.chachae.exam.service.MajorService;
import com.chachae.exam.service.PaperService;
import com.chachae.exam.service.StudentService;
import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * 专业表服务实现类
 *
 * @author chachae
 * @since 2020-02-08 14:26:53
 */
@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class MajorServiceImpl extends ServiceImpl<MajorDAO, Major> implements MajorService {

  private final MajorDAO majorDAO;
  private final PaperService paperService;
  private final AcademyService academyService;
  private final StudentService studentService;

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

  @Async
  @Override
  @Transactional(rollbackFor = Exception.class)
  public void importMajorsExcel(MultipartFile multipartFile) {
    // 同步锁
    synchronized (this) {
      File file = FileUtil.toFile(multipartFile);
      // 读取 Excel 中的数据
      ExcelReader reader = ExcelUtil.getReader(file);
      // 读取专业的信息
      List<Major> majors = reader.read(3, 4, Major.class);
      for (Major major : majors) {
        // 归属学院代码以及专业名称不为空
        if (major.getAcademyId() != null && major.getMajor() != null) {
          // 检测学院代码是否存在，且专业名称是否重复
          boolean isExist = this.academyService.getById(major.getAcademyId()) != null;
          boolean isNotExist = this.academyService.selectByName(major.getMajor()) == null;
          if (isExist && isNotExist) {
            // 插入
            baseMapper.insert(major);
          }
        } else {
          throw new ServiceException("请检查表格数据是否有有误");
        }
      }
    }
  }

  @Override
  public boolean removeById(Serializable id) {
    List<Paper> papers = this.paperService.selectByMajorId((int) id);
    if (CollUtil.isNotEmpty(papers)) {
      throw new ServiceException("专业存在试卷关联，请删除试卷后重试");
    }
    int studentCount = this.studentService.selectCountByMajorId((int) id);
    if (studentCount > 0) {
      throw new ServiceException("专业存在学生关联，请删除相关学生后重试");
    }
    baseMapper.deleteById(id);
    return true;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean save(Major entity) {
    if (CollUtil.isEmpty(this.listByMajorName(entity.getMajor()))) {
      baseMapper.insert(entity);
      return true;
    } else {
      throw new ServiceException("专业已存在，请重重新输入");
    }
  }

  @Override
  public boolean updateById(Major entity) {
    if (CollUtil.isEmpty(this.listByMajorName(entity.getMajor()))) {
      baseMapper.updateById(entity);
      return true;
    } else {
      throw new ServiceException("专业已存在，请重重新输入");
    }
  }
}
