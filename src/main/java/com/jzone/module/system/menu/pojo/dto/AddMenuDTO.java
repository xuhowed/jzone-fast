package com.jzone.module.system.menu.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddMenuDTO {

    private Long parentId = 0L;

    @NotBlank(message = "菜单名称不能为空")
    private String menuName;

    @NotNull(message = "菜单类型不能为空")
    private Integer menuType;

    private String path;
    private String component;
    private String permission;
    private String icon;
    private Integer sort = 0;
    private Integer status = 1;
    private Integer hidden = 0;
    private Integer keepAlive = 0;
}
