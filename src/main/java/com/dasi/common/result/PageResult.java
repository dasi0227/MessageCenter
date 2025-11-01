package com.dasi.common.result;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
public class PageResult<T> {
    private List<T> records;        // 当前页数据
    private long total;             // 总记录数
    private long pageNum;           // 当前页码
    private long pageSize;          // 每页大小
    private long pageCount;         // 总页数

    public static <T> PageResult<T> of(IPage<T> page) {
        PageResult<T> result = new PageResult<>();

        result.setRecords(page.getRecords());
        result.setTotal(page.getTotal());
        result.setPageNum(page.getCurrent());
        result.setPageSize(page.getSize());
        result.setPageCount(page.getPages());

        return result;
    }

    public static <T, R> PageResult<R> of(IPage<T> page, Class<R> clazz) {
        PageResult<R> result = new PageResult<>();
        List<R> list = page.getRecords().stream()
                .map(record -> BeanUtil.copyProperties(record, clazz))
                .toList();

        result.setRecords(list);
        result.setTotal(page.getTotal());
        result.setPageNum(page.getCurrent());
        result.setPageSize(page.getSize());
        result.setPageCount(page.getPages());

        return result;
    }
}
