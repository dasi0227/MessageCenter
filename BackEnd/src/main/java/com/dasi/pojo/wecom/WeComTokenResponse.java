package com.dasi.pojo.wecom;

import lombok.Data;

@Data
public class WeComTokenResponse {
    private Integer errcode;
    private String errmsg;
    private String access_token;
    private Integer expires_in;
}
