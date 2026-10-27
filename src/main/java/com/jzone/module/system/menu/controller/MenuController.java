package com.jzone.module.system.menu.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.jzone.framework.result.ResultBean;
import com.jzone.module.system.menu.pojo.dto.AddMenuDTO;
import com.jzone.module.system.menu.pojo.dto.EditMenuDTO;
import com.jzone.module.system.menu.pojo.vo.MenuVO;
import com.jzone.module.system.menu.pojo.vo.RouteNodeVO;
import com.jzone.module.system.menu.service.MenuService;
import com.jzone.module.system.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sys/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;
    private final UserService userService;

    @GetMapping("/tree")
    @SaCheckPermission("sys:menu:query")
    public ResultBean<List<MenuVO>> tree() {
        return ResultBean.success(menuService.treeAll());
    }

    @GetMapping("/routes")
    public ResultBean<List<RouteNodeVO>> routes() {
        Long userId = StpUtil.getLoginIdAsLong();
        return ResultBean.success(menuService.listUserRoutes(userId));
    }

    @PostMapping
    @SaCheckPermission("sys:menu:create")
    public ResultBean<?> add(@RequestBody @Validated AddMenuDTO dto) {
        menuService.addMenu(dto);
        return ResultBean.success();
    }

    @PutMapping
    @SaCheckPermission("sys:menu:update")
    public ResultBean<?> edit(@RequestBody @Validated EditMenuDTO dto) {
        menuService.editMenu(dto);
        return ResultBean.success();
    }

    @DeleteMapping("/{id}")
    @SaCheckPermission("sys:menu:delete")
    public ResultBean<?> delete(@PathVariable Long id) {
        menuService.deleteMenu(id);
        return ResultBean.success();
    }
}
