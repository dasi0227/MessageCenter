package com.dasi.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatDispatchVO {

    // 操作人维度
    private List<String> accountNames;
    private List<Long> accountCounts;

    // 部门维度
    private List<String> departmentNames;
    private List<Long> departmentCounts;

    // 联系人维度
    private List<String> contactNames;
    private List<Long> contactCounts;

    // 渠道维度
    private List<String> channelNames;
    private List<Long> channelCounts;
}