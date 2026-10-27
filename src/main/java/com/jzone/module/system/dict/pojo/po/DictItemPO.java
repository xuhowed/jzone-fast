package com.jzone.module.system.dict.pojo.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jzone.common.pojo.BasePO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("jz_sys_dict_item")
public class DictItemPO extends BasePO {

    private String dictType;
    private String itemLabel;
    private String itemValue;
    private Integer sort;
    /** 1正常 0禁用 */
    private Integer status;
    private String remark;
}
