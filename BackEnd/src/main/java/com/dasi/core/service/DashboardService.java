package com.dasi.core.service;

import com.dasi.pojo.vo.*;

public interface DashboardService {
    StatTotalVO getStatTotal();

    StatContactVO getStatContact();

    StatChannelVO getStatChannel();

    StatAccountVO getStatAccount();

    StatYearVO getStatYear();

    StatMonthVO getStatMonth();
}
