package com.jzone.module.system.dict.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddDictItemDTO {

    @NotBlank(message = "字典类型不能为空")
    private String dictType;

    @NotBlank(message = "字典标签不能为空")
    private String itemLabel;

    @NotBlank(message = "字典值不能为空")
    private String itemValue;

    private Integer sort = 0;
    private Integer status = 1;
    private String remark;
}
