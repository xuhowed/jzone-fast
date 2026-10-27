package com.jzone.module.system.menu.pojo.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MenuTypeEnum {

    CATALOG(1, "目录"),
    MENU(2, "菜单"),
    BUTTON(3, "按钮");

    private final Integer code;
    private final String desc;

    public static boolean isButton(Integer type) {
        return BUTTON.code.equals(type);
    }

    public static boolean isNotButton(Integer type) {
        return !isButton(type);
    }
}
