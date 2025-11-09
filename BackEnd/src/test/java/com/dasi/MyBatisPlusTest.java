package com.dasi;

import com.dasi.core.mapper.MessageMapper;
import com.dasi.core.service.DashboardService;
import com.dasi.pojo.entity.Message;
import com.dasi.pojo.vo.StatDispatchVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MyBatisPlusTest {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private DashboardService dashboardService;

    @Test
    public void testJson() {
        Message msg = messageMapper.selectById(1987136125542326274L);
        System.out.println(msg);
        System.out.println(msg.getAttachments());
        System.out.println(msg.getContactIds());
    }

    @Test
    public void testDashboard() {
        StatDispatchVO statDispatch = dashboardService.getStatDispatch();
        System.out.println(statDispatch);
    }
}
