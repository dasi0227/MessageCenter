package com.dasi.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payload implements Serializable {
    private Dispatch dispatch;
    private String subject;
    private String content;
    private String attachments;
}