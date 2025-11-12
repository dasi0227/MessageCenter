package com.dasi.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dasi.common.enumeration.MsgStatus;
import com.dasi.pojo.entity.Dispatch;

public interface DispatchService extends IService<Dispatch> {
    void updateStatus(Dispatch dispatch, MsgStatus status, String errorMsg);
}
