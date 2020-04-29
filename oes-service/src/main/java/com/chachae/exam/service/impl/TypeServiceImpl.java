package com.chachae.exam.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chachae.exam.common.dao.TypeDAO;
import com.chachae.exam.common.model.Type;
import com.chachae.exam.service.TypeService;
import org.springframework.stereotype.Service;

/**
 * 试题类型服务实现类
 *
 * @author chachae
 * @since 2020-02-14 18:28:59
 */
@Service
public class TypeServiceImpl extends ServiceImpl<TypeDAO, Type> implements TypeService {

}
