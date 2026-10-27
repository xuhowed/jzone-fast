package com.jzone.module.system.dict.pojo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EditDictTypeDTO {

    @NotNull(message = "ID不能为空")
    private Long id;

    private String dictName;
    private String dictType;
    private Integer status;
    private String remark;
}
