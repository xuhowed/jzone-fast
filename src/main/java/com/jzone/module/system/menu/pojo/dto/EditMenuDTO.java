package com.jzone.module.system.menu.pojo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EditMenuDTO {

    @NotNull(message = "菜单ID不能为空")
    private Long id;

    private Long parentId;
    private String menuName;
    private Integer menuType;
    private String path;
    private String component;
    private String permission;
    private String icon;
    private Integer sort;
    private Integer status;
    private Integer hidden;
    private Integer keepAlive;
}
