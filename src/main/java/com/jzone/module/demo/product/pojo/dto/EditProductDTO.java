package com.jzone.module.demo.product.pojo.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class EditProductDTO {

    @NotNull(message = "id不能为空")
    private Long id;

    @Size(max = 100, message = "商品名称最多100个字符")
    private String productName;

    @Size(max = 50, message = "商品编号最多50个字符")
    private String productCode;

    @Size(max = 50, message = "分类最多50个字符")
    private String category;

    @DecimalMin(value = "0", message = "价格不能为负数")
    private BigDecimal price;

    @Min(value = 0, message = "库存不能为负数")
    private Integer stock;

    private Integer status;

    @Size(max = 500, message = "备注最多500个字符")
    private String remark;
}
