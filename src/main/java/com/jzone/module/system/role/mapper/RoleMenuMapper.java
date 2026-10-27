package com.jzone.module.system.role.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jzone.module.system.role.pojo.po.RoleMenuPO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RoleMenuMapper extends BaseMapper<RoleMenuPO> {

    @Select("SELECT menu_id FROM jz_sys_role_menu WHERE role_id = #{roleId}")
    List<Long> selectMenuIdsByRoleId(@Param("roleId") Long roleId);

    List<Long> selectMenuIdsByActiveRoleIds(@Param("roleIds") List<Long> roleIds);

    @Delete("DELETE FROM jz_sys_role_menu WHERE role_id = #{roleId}")
    void deleteByRoleId(@Param("roleId") Long roleId);

    void insertBatch(@Param("roleId") Long roleId, @Param("menuIds") List<Long> menuIds, @Param("createBy") String createBy);

    List<String> selectPermissionCodesByActiveRoleIds(@Param("roleIds") List<Long> roleIds);

    @Select("SELECT COUNT(1) FROM jz_sys_role_menu WHERE menu_id = #{menuId}")
    long countByMenuId(@Param("menuId") Long menuId);

    @Delete("DELETE FROM jz_sys_role_menu WHERE menu_id = #{menuId}")
    void deleteByMenuId(@Param("menuId") Long menuId);
}
