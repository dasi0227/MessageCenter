package com.dasi.core.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dasi.common.constant.SendConstant;
import com.dasi.common.enumeration.MsgStatus;
import com.dasi.common.exception.SendException;
import com.dasi.core.channel.DlxSender;
import com.dasi.core.mapper.DispatchMapper;
import com.dasi.core.service.DispatchService;
import com.dasi.pojo.entity.Dispatch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class CheckDispatchTask {

    @Autowired
    private DispatchService dispatchService;

    @Autowired
    private DispatchMapper dispatchMapper;

    @Autowired
    private DlxSender dlxSender;

    @Scheduled(cron = "0 0 0/1 * * ?")
    public void checkDispatchStuck() {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);

        List<Dispatch> stuck = dispatchService.list(
                new LambdaQueryWrapper<Dispatch>()
                        .nested(q -> q
                                .and(w1 -> w1
                                        .eq(Dispatch::getStatus, MsgStatus.PROCESSING)
                                        .lt(Dispatch::getProcessAt, oneHourAgo)
                                )
                                .or(w2 -> w2
                                        .eq(Dispatch::getStatus, MsgStatus.SENDING)
                                        .apply("IF(schedule_at IS NOT NULL, schedule_at, created_at) < {0}", oneHourAgo)
                                )
                                .or(w3 -> w3
                                        .eq(Dispatch::getStatus, MsgStatus.PENDING)
                                        .lt(Dispatch::getCreatedAt, oneHourAgo)
                                )
                        )
        );

        stuck.forEach(dispatch -> {
            dispatchService.updateStatus(dispatch, MsgStatus.ERROR, SendConstant.SEND_STUCK);
            dlxSender.sendDlx(dispatch, new SendException(SendConstant.SEND_STUCK));
        });

        log.debug("【CheckDispatchTask】发现任务卡死 {} 条", stuck.size());
    }

    @Scheduled(cron = "0 0 0/1 * * ?")
    public void checkDispatchUnsolved() {
        List<Dispatch> unsolved = dispatchMapper.selectErrorWithoutFailure();

        unsolved.forEach(dispatch -> {
            dispatchService.updateStatus(dispatch, MsgStatus.ERROR, SendConstant.ERROR_UNSOLVED);
            dlxSender.sendDlx(dispatch, new SendException(SendConstant.ERROR_UNSOLVED));
        });

        log.debug("【CheckDispatchTask】发现任务未处理 {} 条", unsolved.size());
    }
}
