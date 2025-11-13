package com.dasi.pojo.vo;

import lombok.Data;

@Data
public class MailboxLoginVO {
    private String name;
    private Long inbox;
    private String phone;
    private String email;
    private String token;
}
