package com.dasi.core.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dasi.common.constant.RedisConstant;
import com.dasi.common.constant.SendConstant;
import com.dasi.common.enumeration.MsgStatus;
import com.dasi.common.exception.SendException;
import com.dasi.core.mapper.DispatchMapper;
import com.dasi.core.service.DispatchService;
import com.dasi.pojo.entity.Dispatch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
public class DispatchServiceImpl extends ServiceImpl<DispatchMapper, Dispatch> implements DispatchService {

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Caching(evict = {
            @CacheEvict(value = RedisConstant.CACHE_DISPATCH_PREFIX, key = "'page:' + #dispatch.messageId"),
            @CacheEvict(value = RedisConstant.CACHE_DASHBOARD_PREFIX, allEntries = true)
    })
    public void updateStatus(Dispatch dispatch, MsgStatus status, String errorMsg) {
        if (status == null) {
            throw new SendException(SendConstant.INVALID_STATUS);
        }
        dispatch.setStatus(status);
        update(new LambdaUpdateWrapper<Dispatch>()
            .eq(Dispatch::getId, dispatch.getId())
            .set(Dispatch::getStatus, status)
            .set(StrUtil.isNotBlank(errorMsg), Dispatch::getErrorMsg, errorMsg)
            .set(status.equals(MsgStatus.SENDING), Dispatch::getSentAt, LocalDateTime.now())
            .set(status.equals(MsgStatus.PROCESSING), Dispatch::getProcessAt, LocalDateTime.now())
            .set(MsgStatus.isFinalStatus(status), Dispatch::getFinishedAt, LocalDateTime.now())
        );
    }

}
