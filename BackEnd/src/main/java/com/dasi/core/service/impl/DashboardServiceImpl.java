package com.dasi.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dasi.common.enumeration.MsgChannel;
import com.dasi.common.enumeration.MsgStatus;
import com.dasi.core.mapper.AccountMapper;
import com.dasi.core.mapper.ContactMapper;
import com.dasi.core.mapper.DispatchMapper;
import com.dasi.core.mapper.MessageMapper;
import com.dasi.core.service.DashboardService;
import com.dasi.pojo.entity.Account;
import com.dasi.pojo.entity.Contact;
import com.dasi.pojo.entity.Dispatch;
import com.dasi.pojo.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private ContactMapper contactMapper;

    @Autowired
    private DispatchMapper dispatchMapper;

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private AccountMapper accountMapper;

    @Override
    public StatTotalVO getStatTotal() {
        long messageTotal = messageMapper.selectCount(null);
        long dispatchTotal = dispatchMapper.selectCount(null);
        long pending = dispatchMapper.selectCount(new LambdaQueryWrapper<Dispatch>().eq(Dispatch::getStatus, MsgStatus.PENDING));
        long sending = dispatchMapper.selectCount(new LambdaQueryWrapper<Dispatch>().eq(Dispatch::getStatus, MsgStatus.SENDING));
        long success = dispatchMapper.selectCount(new LambdaQueryWrapper<Dispatch>().eq(Dispatch::getStatus, MsgStatus.SUCCESS));
        long fail = dispatchMapper.selectCount(new LambdaQueryWrapper<Dispatch>().eq(Dispatch::getStatus, MsgStatus.FAIL));

        StatTotalVO vo = StatTotalVO.builder()
                .messageTotal(messageTotal)
                .dispatchPending(pending)
                .dispatchSending(sending)
                .dispatchTotal(dispatchTotal)
                .dispatchSuccess(success)
                .dispatchFail(fail)
                .build();

        log.info("【Dashboard Service】消息统计：{}", vo);
        return vo;
    }

    @Override
    public StatContactVO getStatContact() {
        long total = contactMapper.selectCount(null);
        long active = contactMapper.selectCount(new LambdaQueryWrapper<Contact>().eq(Contact::getStatus, 1));
        long inactive = contactMapper.selectCount(new LambdaQueryWrapper<Contact>().eq(Contact::getStatus, 0));

        StatContactVO vo = StatContactVO.builder()
                .totalContacts(total)
                .activeContacts(active)
                .inactiveContacts(inactive)
                .build();

        log.info("【Dashboard Service】联系人统计：{}", vo);
        return vo;
    }

    @Override
    public StatChannelVO getStatChannel() {
        List<MsgChannel> channels = Arrays.asList(MsgChannel.values());
        List<Long> counts = channels.stream()
                .map(ch -> dispatchMapper.selectCount(new LambdaQueryWrapper<Dispatch>().eq(Dispatch::getChannel, ch)))
                .toList();

        StatChannelVO vo = StatChannelVO.builder()
                .channels(channels)
                .counts(counts)
                .build();

        log.info("【Dashboard Service】各通道统计：{}", vo);
        return vo;
    }

    @Override
    public StatAccountVO getStatAccount() {
        List<Account> accounts = accountMapper.selectList(
                new LambdaQueryWrapper<Account>().select(Account::getId, Account::getName)
        );

        List<String> names = accounts.stream().map(Account::getName).toList();
        List<Long> counts = accounts.stream()
                .map(acc -> dispatchMapper.selectCount(new LambdaQueryWrapper<Dispatch>().eq(Dispatch::getSendFrom, acc.getId())))
                .toList();

        StatAccountVO vo = StatAccountVO.builder()
                .accounts(names)
                .counts(counts)
                .build();

        log.info("【Dashboard Service】账户统计：{}", vo);
        return vo;
    }

    @Override
    public StatYearVO getStatYear() {
        LocalDate now = LocalDate.now();
        int year = now.getYear();

        Map<Integer, Long> monthCount = dispatchMapper.selectList(null).stream()
                .filter(d -> d.getCreatedAt() != null && d.getCreatedAt().getYear() == year)
                .collect(Collectors.groupingBy(d -> d.getCreatedAt().getMonthValue(), Collectors.counting()));

        List<String> months = new ArrayList<>();
        List<Long> counts = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            months.add(String.format("%02d", i));
            counts.add(monthCount.getOrDefault(i, 0L));
        }

        StatYearVO vo = StatYearVO.builder()
                .months(months)
                .counts(counts)
                .build();

        log.info("【Dashboard Service】年度统计：{}", vo);
        return vo;
    }

    @Override
    public StatMonthVO getStatMonth() {
        LocalDate now = LocalDate.now();
        YearMonth ym = YearMonth.of(now.getYear(), now.getMonth());
        int days = ym.lengthOfMonth();

        Map<Integer, Long> dayCount = dispatchMapper.selectList(null).stream()
                .filter(d -> d.getCreatedAt() != null
                        && d.getCreatedAt().getYear() == now.getYear()
                        && d.getCreatedAt().getMonthValue() == now.getMonthValue())
                .collect(Collectors.groupingBy(d -> d.getCreatedAt().getDayOfMonth(), Collectors.counting()));

        List<String> daysList = new ArrayList<>();
        List<Long> counts = new ArrayList<>();
        for (int i = 1; i <= days; i++) {
            daysList.add(String.format("%02d", i));
            counts.add(dayCount.getOrDefault(i, 0L));
        }

        StatMonthVO vo = StatMonthVO.builder()
                .days(daysList)
                .counts(counts)
                .build();

        log.info("【Dashboard Service】月度统计：{}", vo);
        return vo;
    }
}