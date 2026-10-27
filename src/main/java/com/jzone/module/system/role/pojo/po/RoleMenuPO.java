package com.jzone.module.system.role.pojo.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jzone.common.pojo.BasePO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("jz_sys_role_menu")
public class RoleMenuPO extends BasePO {

    private Long roleId;
    private Long menuId;
}
