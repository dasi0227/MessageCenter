package com.dasi.pojo.wecom;

import lombok.Data;

@Data
public class WeComSendResponse {
    private Integer errcode;
    private String errmsg;
    private String invaliduser;
}
