package com.dasi.core.service;

import com.dasi.pojo.vo.*;

public interface DashboardService {
    StatNumVO getStatNum();

    StatDispatchVO getStatDispatch();

    StatTimelineVO getStatTimeline();
}
