package com.dasi.core.service.impl;

import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dasi.common.constant.RedisConstant;
import com.dasi.common.enumeration.MsgStatus;
import com.dasi.core.service.*;
import com.dasi.pojo.entity.Dispatch;
import com.dasi.pojo.entity.Message;
import com.dasi.pojo.vo.StatDispatchVO;
import com.dasi.pojo.vo.StatNumVO;
import com.dasi.pojo.vo.StatTimelineVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private ContactService contactService;

    @Autowired
    private DispatchService dispatchService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private SensitiveWordService sensitiveWordService;

    @Autowired
    private TemplateService templateService;

    @Autowired
    private RenderService renderService;

    @Override
    @Cacheable(value = RedisConstant.CACHE_DASHBOARD_PREFIX, key = "'num'")
    public StatNumVO getStatNum() {
        // ===== 消息与投递统计 =====
        long messageTotal = messageService.count();
        long dispatchTotal = dispatchService.count();
        long pending = dispatchService.count(new LambdaQueryWrapper<Dispatch>()
                .eq(Dispatch::getStatus, MsgStatus.PENDING));
        long sending = dispatchService.count(new LambdaQueryWrapper<Dispatch>()
                .eq(Dispatch::getStatus, MsgStatus.SENDING));
        long success = dispatchService.count(new LambdaQueryWrapper<Dispatch>()
                .eq(Dispatch::getStatus, MsgStatus.SUCCESS));
        long fail = dispatchService.count(new LambdaQueryWrapper<Dispatch>()
                .eq(Dispatch::getStatus, MsgStatus.FAIL));

        // ===== 人员与组织统计 =====
        long accountNum = accountService.count();
        long departmentNum = departmentService.count();
        long contactNum = contactService.count();

        // ===== 系统配置统计 =====
        long sensitiveWordNum = sensitiveWordService.count();
        long templateNum = templateService.count();
        long renderNum = renderService.count();

        // ===== 封装结果 =====
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
    @Cacheable(value = RedisConstant.CACHE_DASHBOARD_PREFIX, key = "'dispatch'")
    public StatDispatchVO getStatDispatch() {
        Map<String, Long> accountMap = new HashMap<>();
        Map<String, Long> departmentMap = new HashMap<>();
        Map<String, Long> contactMap = new HashMap<>();
        Map<String, Long> channelMap = new HashMap<>();

        // 一次性拉取所有消息并建立映射
        Map<Long, Message> messageMap = messageService.list().stream()
                .collect(Collectors.toMap(Message::getId, m -> m));

        dispatchService.list().forEach(d -> {
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
    @Cacheable(value = RedisConstant.CACHE_DASHBOARD_PREFIX, key = "'timeline'")
    public StatTimelineVO getStatTimeline() {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();
        YearMonth ym = YearMonth.of(year, month);
        int days = ym.lengthOfMonth();

        // 查询所有 Dispatch
        List<Dispatch> dispatchList = dispatchService.list();

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