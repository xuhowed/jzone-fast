package com.jzone.module.demo.product.pojo.dto;

import com.jzone.common.pojo.PageDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class QueryProductDTO extends PageDTO {

    private String productName;

    private String productCode;

    private String category;

    private Integer status;
}
