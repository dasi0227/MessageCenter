package com.dasi.pojo.wecom;

import lombok.Data;

@Data
public class WeComUserIdResponse {
    private Integer errcode;
    private String errmsg;
    private String userid;
}
