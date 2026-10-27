package com.jzone.module.system.dict.pojo.vo;

import lombok.Data;

@Data
public class DictItemVO {

    private Long id;
    private String dictType;
    private String itemLabel;
    private String itemValue;
    private Integer sort;
    private Integer status;
    private String remark;
}
