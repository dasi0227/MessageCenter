package com.dasi.pojo.vo;

import com.dasi.common.enumeration.MsgStatus;
import com.dasi.common.enumeration.MsgChannel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class MessageDetailVO {
    private String msgId;
    private Long dispatchId;
    private MsgChannel channel;
    private MsgStatus status;

    private String subject;
    private String content;

    private Long sendFromId;
    private String sendFromName;
    private Long sendToId;
    private String sendToName;
    private String target;

    private String errorMsg;

    private LocalDateTime scheduleAt;
    private LocalDateTime createdAt;
    private LocalDateTime sentAt;
    private LocalDateTime finishedAt;
}
