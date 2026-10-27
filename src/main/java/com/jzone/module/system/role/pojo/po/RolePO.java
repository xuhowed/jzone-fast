package com.jzone.module.system.role.pojo.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jzone.common.pojo.BasePO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("jz_sys_role")
public class RolePO extends BasePO {

    private String roleName;
    private String roleCode;
    private Integer sort;
    /** 1正常 0禁用 */
    private Integer status;
    private String remark;
}
