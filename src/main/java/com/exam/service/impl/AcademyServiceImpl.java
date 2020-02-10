package com.exam.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exam.entity.Academy;
import com.exam.mapper.AcademyMapper;
import com.exam.service.AcademyService;
import org.springframework.stereotype.Service;

/**
 * 学院服务实现类
 *
 * @author yzn
 * @since 2020-02-09 12:09:59
 */
@Service
public class AcademyServiceImpl extends ServiceImpl<AcademyMapper, Academy>
    implements AcademyService {}
