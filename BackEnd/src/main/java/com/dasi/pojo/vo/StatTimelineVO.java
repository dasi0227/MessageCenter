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
public class StatTimelineVO {
    List<String> months;
    List<Long> monthCounts;
    List<String> days;
    List<Long> dayCounts;
}
