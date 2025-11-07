package com.dasi.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatContactVO {
    private Long totalContacts;
    private Long activeContacts;
    private Long inactiveContacts;
}
