package com.dasi.pojo.vo;

import com.dasi.common.enumeration.MsgStatus;
import com.dasi.common.enumeration.MsgChannel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessagePageVO {
    private Long            msgId;
    private Long            dispatchId;
    private MsgChannel      channel;
    private MsgStatus       status;
    private String          subject;
    private Long            sendFromId;
    private String          sendFromName;
    private Long            sendToId;
    private String          sendToName;
    private String          target;
}