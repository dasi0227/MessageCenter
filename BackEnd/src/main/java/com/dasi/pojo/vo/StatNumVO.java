package com.dasi.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatNumVO {

    // 消息相关
    private Long messageTotal;        // 消息总数
    private Long dispatchTotal;       // 投递总数
    private Long dispatchPending;     // 待处理数
    private Long dispatchSending;     // 发送中数
    private Long dispatchSuccess;     // 成功数
    private Long dispatchFail;        // 失败数

    // 人员相关
    private Long accountNum;          // 账户数量
    private Long departmentNum;       // 部门数量
    private Long contactNum;          // 联系人数量

    // 系统配置相关
    private Long sensitiveWordNum;    // 敏感词数量
    private Long templateNum;         // 模板数量
    private Long renderNum;           // 渲染数量
}