package com.jzone.module.system.role.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jzone.common.constant.CacheKeyConstants;
import com.jzone.common.pojo.PageResult;
import com.jzone.framework.cache.CacheService;
import com.jzone.framework.exception.BusinessException;
import com.jzone.module.system.role.mapper.RoleMapper;
import com.jzone.module.system.role.mapper.RoleMenuMapper;
import com.jzone.module.system.role.pojo.dto.*;
import com.jzone.module.system.role.pojo.po.RolePO;
import com.jzone.module.system.role.pojo.vo.RoleVO;
import com.jzone.module.system.role.service.RoleService;
import com.jzone.module.system.user.mapper.UserRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl extends ServiceImpl<RoleMapper, RolePO> implements RoleService {

    private final RoleMenuMapper roleMenuMapper;
    private final UserRoleMapper userRoleMapper;
    private final CacheService cacheService;

    @Override
    public PageResult<RoleVO> pageRole(QueryRoleDTO dto) {
        Page<RolePO> page = this.lambdaQuery()
                .like(dto.getRoleName() != null, RolePO::getRoleName, dto.getRoleName())
                .like(dto.getRoleCode() != null, RolePO::getRoleCode, dto.getRoleCode())
                .eq(dto.getStatus() != null, RolePO::getStatus, dto.getStatus())
                .page(dto.toPage());
        return PageResult.of(page, BeanUtil.copyToList(page.getRecords(), RoleVO.class));
    }

    @Override
    public RoleVO getRoleById(Long id) {
        RolePO po = this.getById(id);
        if (po == null) {
            throw new BusinessException("角色不存在");
        }
        return BeanUtil.copyProperties(po, RoleVO.class);
    }

    @Override
    public void addRole(AddRoleDTO dto) {
        boolean exists = this.lambdaQuery().eq(RolePO::getRoleCode, dto.getRoleCode()).exists();
        if (exists) {
            throw new BusinessException("角色编码已存在");
        }
        this.save(BeanUtil.copyProperties(dto, RolePO.class));
    }

    @Override
    public void editRole(EditRoleDTO dto) {
        RolePO po = this.getById(dto.getId());
        if (po == null) {
            throw new BusinessException("角色不存在");
        }
        if ("admin".equalsIgnoreCase(po.getRoleCode())) {
            throw new BusinessException("超级管理员角色不允许修改");
        }
        BeanUtil.copyProperties(dto, po, "id");
        this.updateById(po);
        cacheService.evictByPrefix(CacheKeyConstants.MENU_TREE_PREFIX);
        cacheService.evictByPrefix(CacheKeyConstants.MENU_PERMS_PREFIX);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(Long id) {
        RolePO po = this.getById(id);
        if (po == null) {
            throw new BusinessException("角色不存在");
        }
        if ("admin".equalsIgnoreCase(po.getRoleCode())) {
            throw new BusinessException("超级管理员角色不允许删除");
        }
        // 校验是否有用户绑定
        List<Long> userIds = userRoleMapper.selectUserIdsByRoleId(id);
        if (CollUtil.isNotEmpty(userIds)) {
            throw new BusinessException("该角色下存在用户，无法删除");
        }
        this.removeById(id);
        roleMenuMapper.deleteByRoleId(id);
        cacheService.evictByPrefix(CacheKeyConstants.MENU_TREE_PREFIX);
        cacheService.evictByPrefix(CacheKeyConstants.MENU_PERMS_PREFIX);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignMenu(AssignMenuDTO dto) {
        RolePO po = this.getById(dto.getRoleId());
        if (po == null) {
            throw new BusinessException("角色不存在");
        }
        if ("admin".equalsIgnoreCase(po.getRoleCode())) {
            throw new BusinessException("超级管理员角色拥有全部权限，不允许调整");
        }
        roleMenuMapper.deleteByRoleId(dto.getRoleId());
        if (CollUtil.isNotEmpty(dto.getMenuIds())) {
            List<Long> menuIds = dto.getMenuIds().stream().distinct().collect(Collectors.toList());
            roleMenuMapper.insertBatch(dto.getRoleId(), menuIds, "system");
        }
        cacheService.evictByPrefix(CacheKeyConstants.MENU_TREE_PREFIX);
        cacheService.evictByPrefix(CacheKeyConstants.MENU_PERMS_PREFIX);
    }

    @Override
    public List<Long> getMenuIdsByRoleId(Long roleId) {
        return roleMenuMapper.selectMenuIdsByRoleId(roleId);
    }

    @Override
    public List<RoleVO> listAll() {
        List<RolePO> activeRoles = this.lambdaQuery()
                .eq(RolePO::getStatus, 1)
                .orderByAsc(RolePO::getSort)
                .list();
        return BeanUtil.copyToList(activeRoles, RoleVO.class);
    }
}
