package com.dasi.core.service;

import com.dasi.pojo.vo.*;

public interface DashboardService {
    StatNumVO getStatNum();

    StatYearVO getStatYear();

    StatMonthVO getStatMonth();

    StatDispatchVO getStatDispatch();
}
