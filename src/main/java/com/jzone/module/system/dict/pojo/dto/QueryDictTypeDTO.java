package com.jzone.module.system.dict.pojo.dto;

import com.jzone.common.pojo.PageDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class QueryDictTypeDTO extends PageDTO {

    private String dictName;
    private String dictType;
    private Integer status;
}
