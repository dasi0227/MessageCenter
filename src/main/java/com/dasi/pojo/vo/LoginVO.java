package com.dasi.pojo.vo;

import lombok.Data;

@Data
public class LoginVO {
    private AdminVO adminVO;
    private String token;
}
