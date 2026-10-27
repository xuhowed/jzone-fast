package com.jzone.framework.satoken;

import cn.dev33.satoken.stp.StpInterface;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StpInterfaceImpl implements StpInterface {

    private final PermissionLoader permissionLoader;

    public StpInterfaceImpl(@Lazy PermissionLoader permissionLoader) {
        this.permissionLoader = permissionLoader;
    }

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        Long userId = resolveUserId(loginId.toString());
        return permissionLoader.loadPermissions(userId);
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        Long userId = resolveUserId(loginId.toString());
        return permissionLoader.loadRoles(userId);
    }

    private Long resolveUserId(String loginId) {
        try {
            return Long.parseLong(loginId);
        } catch (NumberFormatException e) {
            return permissionLoader.getUserIdByUsername(loginId);
        }
    }
}
