package com.jzone.module.system.role.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jzone.common.pojo.PageResult;
import com.jzone.module.system.role.pojo.dto.*;
import com.jzone.module.system.role.pojo.po.RolePO;
import com.jzone.module.system.role.pojo.vo.RoleVO;

import java.util.List;

public interface RoleService extends IService<RolePO> {

    PageResult<RoleVO> pageRole(QueryRoleDTO dto);

    RoleVO getRoleById(Long id);

    void addRole(AddRoleDTO dto);

    void editRole(EditRoleDTO dto);

    void deleteRole(Long id);

    void assignMenu(AssignMenuDTO dto);

    List<Long> getMenuIdsByRoleId(Long roleId);

    List<RoleVO> listAll();
}
