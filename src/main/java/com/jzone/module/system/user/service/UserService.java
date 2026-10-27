package com.jzone.module.system.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jzone.common.pojo.PageResult;
import com.jzone.module.system.user.pojo.dto.*;
import com.jzone.module.system.user.pojo.po.UserPO;
import com.jzone.module.system.user.pojo.vo.UserVO;

import java.util.List;

public interface UserService extends IService<UserPO> {

    PageResult<UserVO> pageUser(QueryUserDTO dto);

    UserVO getUserById(Long id);

    void addUser(AddUserDTO dto);

    void editUser(EditUserDTO dto);

    void deleteUser(Long id);

    void resetPassword(ResetPasswordDTO dto);

    void assignRole(AssignRoleDTO dto);

    List<String> getPermissionsByUserId(Long userId);

    List<String> getRoleCodesByUserId(Long userId);

    UserPO getByUsername(String username);
}
