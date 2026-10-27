package com.jzone.module.system.menu.pojo.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jzone.common.pojo.BasePO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("jz_sys_menu")
public class MenuPO extends BasePO {

    private Long parentId;
    private String menuName;
    /** 1目录 2菜单 */
    private Integer menuType;
    private String path;
    private String component;
    private String permissionCode;
    private String icon;
    private Integer sort;
    /** 1正常 0禁用 */
    private Integer status;
    /** 0显示 1隐藏 */
    private Integer hidden;
    /** 0不缓存 1缓存 */
    private Integer keepAlive;

    public String getPermission() {
        return permissionCode;
    }

    public void setPermission(String permission) {
        this.permissionCode = permission;
    }
}
