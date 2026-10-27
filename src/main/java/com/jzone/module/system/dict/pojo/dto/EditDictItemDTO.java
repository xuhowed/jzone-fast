package com.jzone.module.system.dict.pojo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EditDictItemDTO {

    @NotNull(message = "ID不能为空")
    private Long id;

    private String itemLabel;
    private String itemValue;
    private Integer sort;
    private Integer status;
    private String remark;
}
