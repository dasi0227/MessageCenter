package com.dasi.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class StatTotalVO {
    private Long messageTotal;
    private Long dispatchTotal;
    private Long dispatchPending;
    private Long dispatchSending;
    private Long dispatchSuccess;
    private Long dispatchFail;
}