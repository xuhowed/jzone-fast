package com.jzone.module.system.user.service.impl;

import java.util.List;

/**
 * 解耦 user 模块对 menu 模块的直接依赖，由 MenuServiceImpl 实现。
 */
public interface MenuPermissionLoader {

    List<String> loadPermissionsByUserId(Long userId);
}
