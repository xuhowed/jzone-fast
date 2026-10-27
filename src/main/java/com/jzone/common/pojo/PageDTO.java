package com.jzone.common.pojo;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.hutool.core.util.StrUtil;
import com.jzone.framework.exception.BusinessException;
import lombok.Getter;
import lombok.Setter;

import java.util.regex.Pattern;

@Getter
@Setter
public class PageDTO {

    private static final Pattern ORDER_BY_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{1,64}$");

    private Integer pageIndex = 1;
    private Integer pageSize = 10;
    private String orderBy;
    private String orderType;

    public <T> Page<T> toPage() {
        if (pageIndex == null || pageIndex < 1) {
            throw new BusinessException("页码必须大于或等于1");
        }
        if (pageSize == null || pageSize < 1 || pageSize > 500) {
            throw new BusinessException("每页条数必须在1到500之间");
        }
        Page<T> page = new Page<>(pageIndex, pageSize);
        if (StrUtil.isNotBlank(orderBy)) {
            String trimmedOrder = orderBy.trim();
            if (!ORDER_BY_PATTERN.matcher(trimmedOrder).matches()) {
                throw new BusinessException("排序字段包含非法字符");
            }
            boolean asc = "asc".equalsIgnoreCase(orderType);
            page.addOrder(asc ? OrderItem.asc(trimmedOrder) : OrderItem.desc(trimmedOrder));
        }
        return page;
    }
}
