package com.jzone.module.system.dict.pojo.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jzone.common.pojo.BasePO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("jz_sys_dict_type")
public class DictTypePO extends BasePO {

    private String dictName;
    private String dictType;
    /** 1正常 0禁用 */
    private Integer status;
    private String remark;
}
