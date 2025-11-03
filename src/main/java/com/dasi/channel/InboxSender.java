package com.dasi.channel;

import com.dasi.core.mapper.DispatchMapper;
import com.dasi.pojo.entity.Dispatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InboxSender {

    @Autowired
    private DispatchMapper dispatchMapper;

    public void send(Long dispatchId) {

    }
}
