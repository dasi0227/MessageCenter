package com.dasi;

import com.dasi.core.mapper.MessageMapper;
import com.dasi.pojo.entity.Message;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MyBatisPlusTest {

    @Autowired
    private MessageMapper messageMapper;

    @Test
    public void testJson() {
        Message msg = messageMapper.selectById(1987136125542326274L);
        System.out.println(msg);
        System.out.println(msg.getAttachments());
        System.out.println(msg.getContactIds());
    }
}
