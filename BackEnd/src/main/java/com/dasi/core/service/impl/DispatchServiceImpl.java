package com.dasi.core.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dasi.common.enumeration.MsgStatus;
import com.dasi.core.mapper.DispatchMapper;
import com.dasi.core.service.DispatchService;
import com.dasi.pojo.entity.Dispatch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
public class DispatchServiceImpl extends ServiceImpl<DispatchMapper, Dispatch> implements DispatchService {

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateFinishStatus(Dispatch dispatch, MsgStatus status, String errorMsg) {
        update(new LambdaUpdateWrapper<Dispatch>()
            .eq(Dispatch::getId, dispatch.getId())
            .set(Dispatch::getStatus, status)
            .set(StrUtil.isNotBlank(errorMsg), Dispatch::getErrorMsg, errorMsg)
            .set(Dispatch::getFinishedAt, LocalDateTime.now())
        );
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateSendStatus(Dispatch dispatch) {
        update(new LambdaUpdateWrapper<Dispatch>()
                .eq(Dispatch::getId, dispatch.getId())
                .set(Dispatch::getSentAt, LocalDateTime.now())
                .set(Dispatch::getStatus, MsgStatus.SENDING)
        );
    }
}
