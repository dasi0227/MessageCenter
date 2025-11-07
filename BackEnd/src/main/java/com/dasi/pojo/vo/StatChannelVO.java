package com.dasi.pojo.vo;

import com.dasi.common.enumeration.MsgChannel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatChannelVO {
    private List<MsgChannel> channels;
    private List<Long> counts;
}
