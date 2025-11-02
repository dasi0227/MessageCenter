package com.dasi.pojo.vo;

import com.dasi.common.enumeration.MsgStatus;
import com.dasi.common.enumeration.MsgChannel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessagePageVO {
    private String msgId;
    private Long dispatchId;
    private MsgChannel channel;
    private MsgStatus status;
    private String subject;
    private String sendFromName;
    private String sendToName;
    private String target;
    private String errorMsg;
    private LocalDateTime createdAt;
}