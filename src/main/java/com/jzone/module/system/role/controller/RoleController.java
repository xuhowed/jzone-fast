package com.jzone.module.system.role.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.jzone.common.pojo.PageResult;
import com.jzone.framework.result.ResultBean;
import com.jzone.module.system.role.pojo.dto.*;
import com.jzone.module.system.role.pojo.vo.RoleVO;
import com.jzone.module.system.role.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sys/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping("/page")
    @SaCheckPermission("sys:role:query")
    public ResultBean<PageResult<RoleVO>> page(QueryRoleDTO dto) {
        return ResultBean.success(roleService.pageRole(dto));
    }

    @GetMapping("/list")
    @SaCheckPermission("sys:role:query")
    public ResultBean<List<RoleVO>> list() {
        return ResultBean.success(roleService.listAll());
    }

    @GetMapping("/{id}")
    @SaCheckPermission("sys:role:query")
    public ResultBean<RoleVO> getById(@PathVariable Long id) {
        return ResultBean.success(roleService.getRoleById(id));
    }

    @PostMapping
    @SaCheckPermission("sys:role:create")
    public ResultBean<?> add(@RequestBody @Validated AddRoleDTO dto) {
        roleService.addRole(dto);
        return ResultBean.success();
    }

    @PutMapping
    @SaCheckPermission("sys:role:update")
    public ResultBean<?> edit(@RequestBody @Validated EditRoleDTO dto) {
        roleService.editRole(dto);
        return ResultBean.success();
    }

    @DeleteMapping("/{id}")
    @SaCheckPermission("sys:role:delete")
    public ResultBean<?> delete(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResultBean.success();
    }

    @PutMapping("/{id}/menus")
    @SaCheckPermission("sys:role:update")
    public ResultBean<?> assignMenu(@PathVariable Long id, @RequestBody @Validated AssignMenuDTO dto) {
        dto.setRoleId(id);
        roleService.assignMenu(dto);
        return ResultBean.success();
    }

    @GetMapping("/{id}/menus")
    @SaCheckPermission("sys:role:query")
    public ResultBean<List<Long>> menuIds(@PathVariable Long id) {
        return ResultBean.success(roleService.getMenuIdsByRoleId(id));
    }
}
