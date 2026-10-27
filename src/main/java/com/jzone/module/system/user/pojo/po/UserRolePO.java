package com.jzone.module.system.user.pojo.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jzone.common.pojo.BasePO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("jz_sys_user_role")
public class UserRolePO extends BasePO {

    private Long userId;
    private Long roleId;
}
