package com.dasi.pojo.wecom;

import lombok.Data;

@Data
public class WeComMediaResponse {
    private int errcode;
    private String errmsg;
    private String type;
    private String media_id;
    private long created_at;
}
