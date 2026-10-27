package com.jzone.module.system.user.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jzone.common.constant.CacheKeyConstants;
import com.jzone.common.pojo.PageResult;
import com.jzone.framework.cache.CacheService;
import com.jzone.framework.exception.BusinessException;
import com.jzone.module.system.role.mapper.RoleMapper;
import com.jzone.module.system.role.pojo.po.RolePO;
import com.jzone.module.system.user.mapper.UserMapper;
import com.jzone.module.system.user.mapper.UserRoleMapper;
import com.jzone.module.system.user.pojo.dto.*;
import com.jzone.module.system.user.pojo.po.UserPO;
import com.jzone.module.system.user.pojo.vo.UserVO;
import com.jzone.module.system.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserPO> implements UserService {

    private final UserRoleMapper userRoleMapper;
    private final RoleMapper roleMapper;
    private final PasswordEncoder passwordEncoder;
    private final MenuPermissionLoader menuPermissionLoader;
    private final CacheService cacheService;

    @Override
    public PageResult<UserVO> pageUser(QueryUserDTO dto) {
        Page<UserPO> page = this.lambdaQuery()
                .like(StrUtil.isNotBlank(dto.getUsername()), UserPO::getUsername, dto.getUsername())
                .like(StrUtil.isNotBlank(dto.getNickname()), UserPO::getNickname, dto.getNickname())
                .eq(dto.getStatus() != null, UserPO::getStatus, dto.getStatus())
                .page(dto.toPage());
        List<UserVO> vos = BeanUtil.copyToList(page.getRecords(), UserVO.class);
        return PageResult.of(page, vos);
    }

    @Override
    public UserVO getUserById(Long id) {
        UserPO po = this.getById(id);
        if (po == null) {
            throw new BusinessException("用户不存在");
        }
        UserVO vo = BeanUtil.copyProperties(po, UserVO.class);
        vo.setRoleIds(userRoleMapper.selectRoleIdsByUserId(id));
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addUser(AddUserDTO dto) {
        boolean exists = this.lambdaQuery().eq(UserPO::getUsername, dto.getUsername()).exists();
        if (exists) {
            throw new BusinessException("用户名已存在");
        }
        UserPO po = BeanUtil.copyProperties(dto, UserPO.class);
        po.setPassword(passwordEncoder.encode(dto.getPassword()));
        this.save(po);
    }

    @Override
    public void editUser(EditUserDTO dto) {
        UserPO po = this.getById(dto.getId());
        if (po == null) {
            throw new BusinessException("用户不存在");
        }
        if ("admin".equalsIgnoreCase(po.getUsername())) {
            throw new BusinessException("超级管理员不允许修改");
        }
        BeanUtil.copyProperties(dto, po, "id", "username", "password");
        this.updateById(po);

        // 如果修改为禁用，强制下线该用户
        if (dto.getStatus() != null && dto.getStatus() == 0) {
            try {
                StpUtil.logout(dto.getId());
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long id) {
        UserPO po = this.getById(id);
        if (po == null) {
            throw new BusinessException("用户不存在");
        }
        if ("admin".equalsIgnoreCase(po.getUsername())) {
            throw new BusinessException("超级管理员不允许删除");
        }
        this.removeById(id);
        userRoleMapper.deleteByUserId(id);

        try {
            StpUtil.logout(id);
        } catch (Exception ignored) {
        }
        cacheService.evict(CacheKeyConstants.menuTreeByUser(id));
        cacheService.evict(CacheKeyConstants.menuPermsByUser(id));
    }

    @Override
    public void resetPassword(ResetPasswordDTO dto) {
        UserPO po = this.getById(dto.getId());
        if (po == null) {
            throw new BusinessException("用户不存在");
        }
        po.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        this.updateById(po);

        // 密码重置后强制重新登录
        try {
            StpUtil.logout(dto.getId());
        } catch (Exception ignored) {
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRole(AssignRoleDTO dto) {
        UserPO po = this.getById(dto.getUserId());
        if (po == null) {
            throw new BusinessException("用户不存在");
        }
        if ("admin".equalsIgnoreCase(po.getUsername())) {
            throw new BusinessException("超级管理员不允许分配角色");
        }
        userRoleMapper.deleteByUserId(dto.getUserId());
        if (CollUtil.isNotEmpty(dto.getRoleIds())) {
            userRoleMapper.insertBatch(dto.getUserId(), dto.getRoleIds());
        }

        // 分配角色后清除该用户的权限与菜单缓存
        cacheService.evict(CacheKeyConstants.menuTreeByUser(dto.getUserId()));
        cacheService.evict(CacheKeyConstants.menuPermsByUser(dto.getUserId()));
    }

    @Override
    public List<String> getPermissionsByUserId(Long userId) {
        return menuPermissionLoader.loadPermissionsByUserId(userId);
    }

    @Override
    public List<String> getRoleCodesByUserId(Long userId) {
        List<Long> roleIds = userRoleMapper.selectRoleIdsByUserId(userId);
        if (CollUtil.isEmpty(roleIds)) {
            return Collections.emptyList();
        }
        return roleMapper.selectBatchIds(roleIds).stream()
                .filter(role -> Integer.valueOf(1).equals(role.getStatus()))
                .map(RolePO::getRoleCode)
                .collect(Collectors.toList());
    }

    @Override
    public UserPO getByUsername(String username) {
        return this.lambdaQuery().eq(UserPO::getUsername, username).one();
    }
}
