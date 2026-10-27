package com.jzone.module.system.menu.pojo.vo;

import lombok.Data;

import java.util.List;

@Data
public class RouteNodeVO {

    private String name;
    private String path;
    private String component;
    private RouteMeta meta;
    private List<RouteNodeVO> children;

    @Data
    public static class RouteMeta {
        private String title;
        private String icon;
        private Boolean keepAlive;
        private Boolean hidden;
    }
}
