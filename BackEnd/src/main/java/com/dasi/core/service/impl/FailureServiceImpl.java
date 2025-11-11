package com.dasi.core.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dasi.core.mapper.FailureMapper;
import com.dasi.core.service.FailureService;
import com.dasi.pojo.entity.Failure;
import org.springframework.stereotype.Service;

@Service
public class FailureServiceImpl extends ServiceImpl<FailureMapper, Failure> implements FailureService {

}
