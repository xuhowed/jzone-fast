package com.jzone.module.system.user.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.jzone.common.pojo.PageResult;
import com.jzone.framework.result.ResultBean;
import com.jzone.module.system.user.pojo.dto.*;
import com.jzone.module.system.user.pojo.vo.UserVO;
import com.jzone.module.system.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sys/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/page")
    @SaCheckPermission("sys:user:query")
    public ResultBean<PageResult<UserVO>> page(QueryUserDTO dto) {
        return ResultBean.success(userService.pageUser(dto));
    }

    @GetMapping("/{id}")
    @SaCheckPermission("sys:user:query")
    public ResultBean<UserVO> getById(@PathVariable Long id) {
        return ResultBean.success(userService.getUserById(id));
    }

    @PostMapping
    @SaCheckPermission("sys:user:create")
    public ResultBean<?> add(@RequestBody @Validated AddUserDTO dto) {
        userService.addUser(dto);
        return ResultBean.success();
    }

    @PutMapping
    @SaCheckPermission("sys:user:update")
    public ResultBean<?> edit(@RequestBody @Validated EditUserDTO dto) {
        userService.editUser(dto);
        return ResultBean.success();
    }

    @DeleteMapping("/{id}")
    @SaCheckPermission("sys:user:delete")
    public ResultBean<?> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResultBean.success();
    }

    @PutMapping("/{id}/password")
    @SaCheckPermission("sys:user:update")
    public ResultBean<?> resetPassword(@PathVariable Long id, @RequestBody @Validated ResetPasswordDTO dto) {
        dto.setId(id);
        userService.resetPassword(dto);
        return ResultBean.success();
    }

    @PutMapping("/{id}/roles")
    @SaCheckPermission("sys:user:update")
    public ResultBean<?> assignRole(@PathVariable Long id, @RequestBody @Validated AssignRoleDTO dto) {
        dto.setUserId(id);
        userService.assignRole(dto);
        return ResultBean.success();
    }
}
