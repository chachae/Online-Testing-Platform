package com.exam.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exam.entity.StuAnswerRecord;
import com.exam.mapper.StuAnswerRecordMapper;
import com.exam.service.StuAnswerRecordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * (StuAnswerRecord)表服务实现类
 *
 * @author yzn
 * @since 2020-02-07 21:49:52
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class StuAnswerRecordServiceImpl extends ServiceImpl<StuAnswerRecordMapper, StuAnswerRecord>
    implements StuAnswerRecordService {}
