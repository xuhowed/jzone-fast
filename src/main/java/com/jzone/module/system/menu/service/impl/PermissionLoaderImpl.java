package com.jzone.module.system.menu.service.impl;

import com.jzone.framework.exception.BusinessException;
import com.jzone.framework.satoken.PermissionLoader;
import com.jzone.module.system.role.mapper.RoleMapper;
import com.jzone.module.system.role.pojo.po.RolePO;
import com.jzone.module.system.user.mapper.UserMapper;
import com.jzone.module.system.user.mapper.UserRoleMapper;
import com.jzone.module.system.user.pojo.po.UserPO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PermissionLoaderImpl implements PermissionLoader {

    private final MenuServiceImpl menuService;
    private final UserRoleMapper userRoleMapper;
    private final RoleMapper roleMapper;
    private final UserMapper userMapper;

    @Override
    public Long getUserIdByUsername(String username) {
        UserPO user = userMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<UserPO>()
                        .eq(UserPO::getUsername, username)
                        .select(UserPO::getId)
        );
        if (user == null) {
            throw new BusinessException("用户不存在: " + username);
        }
        return user.getId();
    }

    @Override
    public List<String> loadPermissions(Long userId) {
        return menuService.loadPermissionsByUserId(userId);
    }

    @Override
    public List<String> loadRoles(Long userId) {
        List<Long> roleIds = userRoleMapper.selectRoleIdsByUserId(userId);
        if (roleIds.isEmpty()) {
            return Collections.emptyList();
        }
        return roleMapper.selectBatchIds(roleIds).stream()
                .filter(role -> Integer.valueOf(1).equals(role.getStatus()))
                .map(RolePO::getRoleCode)
                .collect(Collectors.toList());
    }
}
