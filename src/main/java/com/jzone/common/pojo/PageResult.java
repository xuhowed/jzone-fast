package com.jzone.common.pojo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PageResult<T> {

    private List<T> records;
    private long total;
    private long pageIndex;
    private long pageSize;
    private long pages;

    public static <T> PageResult<T> of(Page<T> page) {
        PageResult<T> result = new PageResult<>();
        result.setRecords(page.getRecords());
        result.setTotal(page.getTotal());
        result.setPageIndex(page.getCurrent());
        result.setPageSize(page.getSize());
        result.setPages(page.getPages());
        return result;
    }

    public static <T> PageResult<T> of(Page<?> page, List<T> records) {
        PageResult<T> result = new PageResult<>();
        result.setRecords(records);
        result.setTotal(page.getTotal());
        result.setPageIndex(page.getCurrent());
        result.setPageSize(page.getSize());
        result.setPages(page.getPages());
        return result;
    }
}
