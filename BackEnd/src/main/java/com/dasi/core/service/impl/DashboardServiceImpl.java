package com.dasi.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dasi.common.enumeration.MsgChannel;
import com.dasi.common.enumeration.MsgStatus;
import com.dasi.core.mapper.*;
import com.dasi.core.service.DashboardService;
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

    @Autowired
    private DepartmentMapper departmentMapper;

    @Autowired
    private SensitiveWordMapper sensitiveWordMapper;

    @Override
    public StatNumVO getStatNum() {
        // 消息与投递统计
        long messageTotal = messageMapper.selectCount(null);
        long dispatchTotal = dispatchMapper.selectCount(null);
        long pending = dispatchMapper.selectCount(new LambdaQueryWrapper<Dispatch>().eq(Dispatch::getStatus, MsgStatus.PENDING));
        long sending = dispatchMapper.selectCount(new LambdaQueryWrapper<Dispatch>().eq(Dispatch::getStatus, MsgStatus.SENDING));
        long success = dispatchMapper.selectCount(new LambdaQueryWrapper<Dispatch>().eq(Dispatch::getStatus, MsgStatus.SUCCESS));
        long fail = dispatchMapper.selectCount(new LambdaQueryWrapper<Dispatch>().eq(Dispatch::getStatus, MsgStatus.FAIL));

        // 人员与组织统计
        long accountNum = accountMapper.selectCount(null);
        long departmentNum = departmentMapper.selectCount(null);
        long contactNum = contactMapper.selectCount(null);

        // 系统配置统计
        long sensitiveWordNum = sensitiveWordMapper.selectCount(null);
//        long templateNum = templateMapper.selectCount(null);
        long templateNum = 0L;
        long channelNum = Arrays.stream(MsgChannel.values()).count();

        // 封装结果
        StatNumVO vo = StatNumVO.builder()
                .messageTotal(messageTotal)
                .dispatchTotal(dispatchTotal)
                .dispatchPending(pending)
                .dispatchSending(sending)
                .dispatchSuccess(success)
                .dispatchFail(fail)
                .accountNum(accountNum)
                .departmentNum(departmentNum)
                .contactNum(contactNum)
                .sensitiveWordNum(sensitiveWordNum)
                .templateNum(templateNum)
                .channelNum(channelNum)
                .build();

        log.debug("【Dashboard Service】数量统计：{}", vo);
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

    @Override
    public StatDispatchVO getStatDispatch() {
        List<Map<String, Object>> accountList = dispatchMapper.countByAccount();
        List<Map<String, Object>> departmentList = dispatchMapper.countByDepartment();
        List<Map<String, Object>> contactList = dispatchMapper.countByContact();
        List<Map<String, Object>> channelList = dispatchMapper.countByChannel();

        StatDispatchVO vo = StatDispatchVO.builder()
                .accountNames(accountList.stream().map(m -> m.get("name").toString()).toList())
                .accountCounts(accountList.stream().map(m -> ((Number) m.get("count")).longValue()).toList())
                .departmentNames(departmentList.stream().map(m -> m.get("name").toString()).toList())
                .departmentCounts(departmentList.stream().map(m -> ((Number) m.get("count")).longValue()).toList())
                .contactNames(contactList.stream().map(m -> m.get("name").toString()).toList())
                .contactCounts(contactList.stream().map(m -> ((Number) m.get("count")).longValue()).toList())
                .channelNames(channelList.stream().map(m -> m.get("name").toString()).toList())
                .channelCounts(channelList.stream().map(m -> ((Number) m.get("count")).longValue()).toList())
                .build();

        log.debug("【Dashboard Service】分发统计：{}", vo);
        return vo;
    }
}