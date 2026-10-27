package com.jzone.module.system.menu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jzone.module.system.menu.pojo.dto.AddMenuDTO;
import com.jzone.module.system.menu.pojo.dto.EditMenuDTO;
import com.jzone.module.system.menu.pojo.po.MenuPO;
import com.jzone.module.system.menu.pojo.vo.MenuVO;
import com.jzone.module.system.menu.pojo.vo.RouteNodeVO;

import java.util.List;

public interface MenuService extends IService<MenuPO> {

    List<MenuVO> treeAll();

    List<RouteNodeVO> listUserRoutes(Long userId);

    List<String> getPermissionsByUserId(Long userId);

    void addMenu(AddMenuDTO dto);

    void editMenu(EditMenuDTO dto);

    void deleteMenu(Long id);
}
