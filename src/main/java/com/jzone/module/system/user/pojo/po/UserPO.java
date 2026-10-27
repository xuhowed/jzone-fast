package com.jzone.module.system.user.pojo.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jzone.common.pojo.BasePO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("jz_sys_user")
public class UserPO extends BasePO {

    private String username;
    private String password;
    private String nickname;
    private String email;
    private String phone;
    /** 1正常 0禁用 */
    private Integer status;
}
