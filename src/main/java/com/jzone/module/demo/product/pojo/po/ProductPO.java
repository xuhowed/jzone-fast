package com.jzone.module.demo.product.pojo.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jzone.common.pojo.BasePO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 商品管理
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("jz_demo_product")
public class ProductPO extends BasePO {

    private String productName;

    private String productCode;

    private String category;

    private BigDecimal price;

    private Integer stock;

    private Integer status;

    private String remark;
}
