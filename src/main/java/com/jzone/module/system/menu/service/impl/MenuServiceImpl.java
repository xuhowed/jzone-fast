package com.jzone.module.system.menu.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jzone.common.constant.CacheKeyConstants;
import com.jzone.framework.cache.CacheService;
import com.jzone.framework.exception.BusinessException;
import com.jzone.module.system.menu.mapper.MenuMapper;
import com.jzone.module.system.menu.pojo.dto.AddMenuDTO;
import com.jzone.module.system.menu.pojo.dto.EditMenuDTO;
import com.jzone.module.system.menu.pojo.enums.MenuTypeEnum;
import com.jzone.module.system.menu.pojo.po.MenuPO;
import com.jzone.module.system.menu.pojo.vo.MenuVO;
import com.jzone.module.system.menu.pojo.vo.RouteNodeVO;
import com.jzone.module.system.menu.service.MenuService;
import com.jzone.module.system.role.mapper.RoleMapper;
import com.jzone.module.system.role.mapper.RoleMenuMapper;
import com.jzone.module.system.user.mapper.UserRoleMapper;
import com.jzone.module.system.user.service.impl.MenuPermissionLoader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl extends ServiceImpl<MenuMapper, MenuPO>
        implements MenuService, MenuPermissionLoader {

    private final RoleMenuMapper roleMenuMapper;
    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;
    private final CacheService cacheService;

    @Override
    public List<MenuVO> treeAll() {
        List<MenuPO> menus = this.lambdaQuery().orderByAsc(MenuPO::getSort).list();
        List<MenuVO> allVos = menus.stream().map(m -> {
            MenuVO vo = new MenuVO();
            vo.setId(m.getId());
            vo.setParentId(m.getParentId());
            vo.setMenuName(m.getMenuName());
            vo.setMenuType(m.getMenuType());
            vo.setPath(m.getPath());
            vo.setComponent(m.getComponent());
            vo.setPermission(m.getPermissionCode());
            vo.setIcon(m.getIcon());
            vo.setSort(m.getSort());
            vo.setStatus(m.getStatus());
            vo.setHidden(m.getHidden());
            vo.setKeepAlive(m.getKeepAlive());
            return vo;
        }).collect(Collectors.toList());

        return buildTree(allVos, 0L);
    }

    @Override
    public List<RouteNodeVO> listUserRoutes(Long userId) {
        String key = CacheKeyConstants.menuTreeByUser(userId);
        return cacheService.getOrLoad(key, List.class, () -> {
            boolean isAdmin = checkIsAdmin(userId);
            List<MenuPO> activeMenus = this.lambdaQuery()
                    .eq(MenuPO::getStatus, 1)
                    .orderByAsc(MenuPO::getSort)
                    .list();

            List<MenuPO> permittedMenus;
            if (isAdmin) {
                permittedMenus = activeMenus;
            } else {
                List<Long> roleIds = userRoleMapper.selectRoleIdsByUserId(userId);
                if (CollUtil.isEmpty(roleIds)) {
                    return Collections.emptyList();
                }
                List<Long> userMenuIds = roleMenuMapper.selectMenuIdsByActiveRoleIds(roleIds);
                Set<Long> accessibleMenuIds = new HashSet<>(userMenuIds);

                // 递归回溯保留所有父级目录，确保层级导航完整
                Map<Long, MenuPO> menuMap = activeMenus.stream()
                        .collect(Collectors.toMap(MenuPO::getId, m -> m));
                Set<Long> finalMenuIds = new HashSet<>();
                for (Long menuId : accessibleMenuIds) {
                    Long current = menuId;
                    while (current != null && current != 0L) {
                        finalMenuIds.add(current);
                        MenuPO parent = menuMap.get(current);
                        current = (parent != null) ? parent.getParentId() : null;
                    }
                }

                permittedMenus = activeMenus.stream()
                        .filter(m -> finalMenuIds.contains(m.getId()))
                        .collect(Collectors.toList());
            }

            // 过滤按钮，仅保留目录与页面菜单构建路由
            List<MenuVO> vos = permittedMenus.stream()
                    .filter(m -> MenuTypeEnum.isNotButton(m.getMenuType()))
                    .map(m -> {
                        MenuVO vo = new MenuVO();
                        vo.setId(m.getId());
                        vo.setParentId(m.getParentId());
                        vo.setMenuName(m.getMenuName());
                        vo.setMenuType(m.getMenuType());
                        vo.setPath(m.getPath());
                        vo.setComponent(m.getComponent());
                        vo.setPermission(m.getPermissionCode());
                        vo.setIcon(m.getIcon());
                        vo.setSort(m.getSort());
                        vo.setStatus(m.getStatus());
                        vo.setHidden(m.getHidden());
                        vo.setKeepAlive(m.getKeepAlive());
                        return vo;
                    })
                    .collect(Collectors.toList());

            List<MenuVO> tree = buildTree(vos, 0L);
            return convertToRoutes(tree);
        });
    }

    @Override
    public List<String> getPermissionsByUserId(Long userId) {
        return loadPermissionsByUserId(userId);
    }

    @Override
    public List<String> loadPermissionsByUserId(Long userId) {
        String key = CacheKeyConstants.menuPermsByUser(userId);
        return cacheService.getOrLoad(key, List.class, () -> {
            if (checkIsAdmin(userId)) {
                return this.lambdaQuery()
                        .eq(MenuPO::getStatus, 1)
                        .isNotNull(MenuPO::getPermissionCode)
                        .ne(MenuPO::getPermissionCode, "")
                        .list()
                        .stream()
                        .map(MenuPO::getPermissionCode)
                        .distinct()
                        .collect(Collectors.toList());
            }
            List<Long> roleIds = userRoleMapper.selectRoleIdsByUserId(userId);
            if (CollUtil.isEmpty(roleIds)) {
                return Collections.emptyList();
            }
            return roleMenuMapper.selectPermissionCodesByActiveRoleIds(roleIds);
        });
    }

    @Override
    public void addMenu(AddMenuDTO dto) {
        MenuPO po = new MenuPO();
        po.setParentId(dto.getParentId() != null ? dto.getParentId() : 0L);
        po.setMenuName(dto.getMenuName());
        po.setMenuType(dto.getMenuType());
        po.setPath(dto.getPath());
        po.setComponent(dto.getComponent());
        po.setPermissionCode(dto.getPermission());
        po.setIcon(dto.getIcon());
        po.setSort(dto.getSort() != null ? dto.getSort() : 0);
        po.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        po.setHidden(dto.getHidden() != null ? dto.getHidden() : 0);
        po.setKeepAlive(dto.getKeepAlive() != null ? dto.getKeepAlive() : 0);
        this.save(po);
        evictMenuCaches();
    }

    @Override
    public void editMenu(EditMenuDTO dto) {
        MenuPO po = this.getById(dto.getId());
        if (po == null) {
            throw new BusinessException("菜单不存在");
        }
        if (dto.getParentId() != null) po.setParentId(dto.getParentId());
        if (dto.getMenuName() != null) po.setMenuName(dto.getMenuName());
        if (dto.getMenuType() != null) po.setMenuType(dto.getMenuType());
        po.setPath(dto.getPath());
        po.setComponent(dto.getComponent());
        po.setPermissionCode(dto.getPermission());
        po.setIcon(dto.getIcon());
        if (dto.getSort() != null) po.setSort(dto.getSort());
        if (dto.getStatus() != null) po.setStatus(dto.getStatus());
        if (dto.getHidden() != null) po.setHidden(dto.getHidden());
        if (dto.getKeepAlive() != null) po.setKeepAlive(dto.getKeepAlive());
        this.updateById(po);
        evictMenuCaches();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMenu(Long id) {
        MenuPO po = this.getById(id);
        if (po == null) {
            throw new BusinessException("菜单不存在");
        }
        boolean hasChild = this.lambdaQuery().eq(MenuPO::getParentId, id).exists();
        if (hasChild) {
            throw new BusinessException("存在子节点，无法删除");
        }
        this.removeById(id);
        roleMenuMapper.deleteByMenuId(id);
        evictMenuCaches();
    }

    private boolean checkIsAdmin(Long userId) {
        List<Long> roleIds = userRoleMapper.selectRoleIdsByUserId(userId);
        if (CollUtil.isEmpty(roleIds)) {
            return false;
        }
        return roleMapper.selectBatchIds(roleIds).stream()
                .anyMatch(role -> Integer.valueOf(1).equals(role.getStatus())
                        && "admin".equalsIgnoreCase(role.getRoleCode()));
    }

    private void evictMenuCaches() {
        cacheService.evictByPrefix(CacheKeyConstants.MENU_TREE_PREFIX);
        cacheService.evictByPrefix(CacheKeyConstants.MENU_PERMS_PREFIX);
    }

    private List<MenuVO> buildTree(List<MenuVO> all, Long parentId) {
        List<MenuVO> result = new ArrayList<>();
        for (MenuVO vo : all) {
            if (parentId.equals(vo.getParentId())) {
                vo.setChildren(buildTree(all, vo.getId()));
                result.add(vo);
            }
        }
        return result;
    }

    private List<RouteNodeVO> convertToRoutes(List<MenuVO> menus) {
        List<RouteNodeVO> routes = new ArrayList<>();
        for (MenuVO menu : menus) {
            RouteNodeVO route = new RouteNodeVO();
            route.setName(menu.getMenuName());
            route.setPath(menu.getPath());
            route.setComponent(menu.getComponent());
            RouteNodeVO.RouteMeta meta = new RouteNodeVO.RouteMeta();
            meta.setTitle(menu.getMenuName());
            meta.setIcon(menu.getIcon());
            meta.setKeepAlive(menu.getKeepAlive() != null && menu.getKeepAlive() == 1);
            meta.setHidden(menu.getHidden() != null && menu.getHidden() == 1);
            route.setMeta(meta);
            if (CollUtil.isNotEmpty(menu.getChildren())) {
                route.setChildren(convertToRoutes(menu.getChildren()));
            }
            routes.add(route);
        }
        return routes;
    }
}
