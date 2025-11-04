package com.dasi.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dasi.common.enumeration.MsgStatus;
import com.dasi.core.mapper.DispatchMapper;
import com.dasi.core.service.DispatchService;
import com.dasi.pojo.entity.Dispatch;
import com.dasi.pojo.entity.Mailbox;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
public class DispatchServiceImpl extends ServiceImpl<DispatchMapper, Dispatch> implements DispatchService {

    @Autowired
    private DispatchMapper dispatchMapper;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateFinishStatus(Long id, MsgStatus status, String errorMsg) {
        update(new LambdaUpdateWrapper<Dispatch>()
            .eq(Dispatch::getId, id)
            .set(Dispatch::getStatus, status)
            .set(Dispatch::getErrorMsg, errorMsg)
            .set(Dispatch::getFinishedAt, LocalDateTime.now())
        );
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void updateSendStatus(Long dispatchId) {
        update(new LambdaUpdateWrapper<Dispatch>()
                .eq(Dispatch::getId, dispatchId)
                .set(Dispatch::getStatus, MsgStatus.SENDING)
                .set(Dispatch::getSentAt, LocalDateTime.now())
        );
    }

    @Override
    public void updateFailStatus(Long dispatchId, String errorMsg) {
        update(new LambdaUpdateWrapper<Dispatch>()
                .eq(Dispatch::getId, dispatchId)
                .set(Dispatch::getStatus, MsgStatus.FAIL)
                .set(Dispatch::getErrorMsg, errorMsg)
                .set(Dispatch::getFinishedAt, LocalDateTime.now())
        );
    }

    @Override
    public Mailbox selectMailboxInfo(Long dispatchId) {
        return dispatchMapper.selectMailboxInfo(dispatchId);
    }
}
