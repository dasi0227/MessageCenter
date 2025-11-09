package com.dasi.core.service.impl;

import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dasi.common.enumeration.MsgStatus;
import com.dasi.core.mapper.*;
import com.dasi.core.service.DashboardService;
import com.dasi.pojo.entity.Dispatch;
import com.dasi.pojo.entity.Message;
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

    @Autowired
    private TemplateMapper templateMapper;

    @Autowired
    private RenderMapper renderMapper;

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
        long templateNum = templateMapper.selectCount(null);
        long renderNum = renderMapper.selectCount(null);

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
                .renderNum(renderNum)
                .build();

        log.debug("【Dashboard Service】数量统计：{}", vo);
        return vo;
    }

    @Override
    public StatDispatchVO getStatDispatch() {
        Map<String, Long> accountMap = new HashMap<>();
        Map<String, Long> departmentMap = new HashMap<>();
        Map<String, Long> contactMap = new HashMap<>();
        Map<String, Long> channelMap = new HashMap<>();

        Map<Long, Message> messageMap = messageMapper.selectList(null).stream()
                .collect(Collectors.toMap(Message::getId, m -> m));

        dispatchMapper.selectList(null).forEach(d -> {
            Message m = messageMap.get(d.getMessageId());
            if (m == null) return;

            accountMap.merge(m.getAccountName(), 1L, Long::sum);
            departmentMap.merge(m.getDepartmentName(), 1L, Long::sum);
            contactMap.merge(d.getContactName(), 1L, Long::sum);
            channelMap.merge(m.getChannel().name(), 1L, Long::sum);
        });

        var sortedAccountMap = MapUtil.sortByValue(accountMap, true);
        var sortedDepartmentMap = MapUtil.sortByValue(departmentMap, true);
        var sortedContactMap = MapUtil.sortByValue(contactMap, true);
        var sortedChannelMap = MapUtil.sortByValue(channelMap, true);

        return StatDispatchVO.builder()
                .accountNames(new ArrayList<>(sortedAccountMap.keySet()))
                .accountCounts(new ArrayList<>(sortedAccountMap.values()))
                .departmentNames(new ArrayList<>(sortedDepartmentMap.keySet()))
                .departmentCounts(new ArrayList<>(sortedDepartmentMap.values()))
                .contactNames(new ArrayList<>(sortedContactMap.keySet()))
                .contactCounts(new ArrayList<>(sortedContactMap.values()))
                .channelNames(new ArrayList<>(sortedChannelMap.keySet()))
                .channelCounts(new ArrayList<>(sortedChannelMap.values()))
                .build();
    }

    @Override
    public StatTimelineVO getStatTimeline() {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();
        YearMonth ym = YearMonth.of(year, month);
        int days = ym.lengthOfMonth();

        // 查询一次即可
        List<Dispatch> dispatchList = dispatchMapper.selectList(null);

        // ===== 年度统计 =====
        Map<Integer, Long> monthCount = dispatchList.stream()
                .filter(d -> d.getCreatedAt() != null && d.getCreatedAt().getYear() == year)
                .collect(Collectors.groupingBy(d -> d.getCreatedAt().getMonthValue(), Collectors.counting()));

        List<String> months = new ArrayList<>();
        List<Long> monthCounts = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            months.add(String.format("%02d", i));
            monthCounts.add(monthCount.getOrDefault(i, 0L));
        }

        // ===== 月度统计 =====
        Map<Integer, Long> dayCount = dispatchList.stream()
                .filter(d -> d.getCreatedAt() != null
                        && d.getCreatedAt().getYear() == year
                        && d.getCreatedAt().getMonthValue() == month)
                .collect(Collectors.groupingBy(d -> d.getCreatedAt().getDayOfMonth(), Collectors.counting()));

        List<String> daysList = new ArrayList<>();
        List<Long> dayCounts = new ArrayList<>();
        for (int i = 1; i <= days; i++) {
            daysList.add(String.format("%02d", i));
            dayCounts.add(dayCount.getOrDefault(i, 0L));
        }

        // ===== 构建结果 =====
        return StatTimelineVO.builder()
                .months(months)
                .monthCounts(monthCounts)
                .days(daysList)
                .dayCounts(dayCounts)
                .build();
    }
}