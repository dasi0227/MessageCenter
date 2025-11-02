package com.dasi.core.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dasi.common.enumeration.MsgStatus;
import com.dasi.common.enumeration.ResultInfo;
import com.dasi.common.exception.SendException;
import com.dasi.common.properties.RabbitMqProperties;
import com.dasi.common.result.PageResult;
import com.dasi.core.mapper.ContactMapper;
import com.dasi.core.mapper.DispatchMapper;
import com.dasi.core.mapper.MessageMapper;
import com.dasi.pojo.dto.MessagePageDTO;
import com.dasi.pojo.dto.MessageSendDTO;
import com.dasi.pojo.entity.Contact;
import com.dasi.pojo.entity.Dispatch;
import com.dasi.pojo.entity.Message;
import com.dasi.pojo.vo.MessageDetailVO;
import com.dasi.pojo.vo.MessagePageVO;
import com.dasi.util.UserContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private DispatchMapper dispatchMapper;

    @Autowired
    private ContactMapper contactMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitMqProperties rabbitMqProperties;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendMessage(MessageSendDTO dto) {
        // 1. 创建消息
        Message message = new Message();
        BeanUtils.copyProperties(dto, message);
        if (!save(message)) {
            throw new SendException(ResultInfo.MESSAGE_SAVE_ERROR);
        }

        // 2. 发到每个收件人
        Long sendFrom = UserContextUtil.getUser();
        for (Long sendTo : dto.getContactIds()) {
            // 3. 渠道
            Contact contact = contactMapper.selectById(sendTo);
            String target = switch (dto.getChannel()) {
                case EMAIL -> contact.getEmail();
                case SMS -> contact.getPhone();
                case INBOX -> contact.getInbox().toString();
            };

            // 4. 发送
            Dispatch dispatch = Dispatch.builder()
                    .msgId(message.getId()).status(MsgStatus.PENDING)
                    .sendFrom(sendFrom).sendTo(sendTo).target(target)
                    .build();
            dispatchMapper.insert(dispatch);

            // 5. 推送到消息队列
            String key = dto.getChannel().getRoute(rabbitMqProperties) + dto.getChannel().name().toLowerCase();
            rabbitTemplate.convertAndSend(rabbitMqProperties.getExchange(), key, dispatch.getId());
        }

        log.debug("【Message Service】发送消息：{}", dto);
    }

    @Override
    public PageResult<MessagePageVO> getMessagePage(MessagePageDTO dto) {
        Page<MessagePageVO> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        IPage<MessagePageVO> result = messageMapper.selectMessagePage(page, dto);
        log.debug("【Message Service】分页查询消息：{}", dto);
        return PageResult.of(result);
    }

    @Override
    public MessageDetailVO getMessageDetail(Long dispatchId) {
        MessageDetailVO messageDetailVO = messageMapper.selectMessageDetail(dispatchId);
        log.debug("【Message Service】查询消息详情：{}", messageDetailVO);
        return messageDetailVO;
    }
}
