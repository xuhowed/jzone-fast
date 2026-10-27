package com.jzone.framework.satoken;

import java.util.List;

/**
 * 权限/角色加载接口，由 system 模块实现，避免框架层直接依赖业务层。
 */
public interface PermissionLoader {

    Long getUserIdByUsername(String username);

    List<String> loadPermissions(Long userId);

    List<String> loadRoles(Long userId);
}
