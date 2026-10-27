package com.jzone.module.system.dict.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.jzone.common.pojo.PageResult;
import com.jzone.framework.result.ResultBean;
import com.jzone.module.system.dict.pojo.dto.*;
import com.jzone.module.system.dict.pojo.vo.DictItemVO;
import com.jzone.module.system.dict.pojo.vo.DictTypeVO;
import com.jzone.module.system.dict.service.DictItemService;
import com.jzone.module.system.dict.service.DictTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sys/dict")
@RequiredArgsConstructor
public class DictController {

    private final DictTypeService dictTypeService;
    private final DictItemService dictItemService;

    // ---- 字典类型 ----

    @GetMapping("/type/page")
    @SaCheckPermission("sys:dict:query")
    public ResultBean<PageResult<DictTypeVO>> typePage(QueryDictTypeDTO dto) {
        return ResultBean.success(dictTypeService.pageDictType(dto));
    }

    @PostMapping("/type")
    @SaCheckPermission("sys:dict:create")
    public ResultBean<?> addType(@RequestBody @Validated AddDictTypeDTO dto) {
        dictTypeService.addDictType(dto);
        return ResultBean.success();
    }

    @PutMapping("/type")
    @SaCheckPermission("sys:dict:update")
    public ResultBean<?> editType(@RequestBody @Validated EditDictTypeDTO dto) {
        dictTypeService.editDictType(dto);
        return ResultBean.success();
    }

    @DeleteMapping("/type/{id}")
    @SaCheckPermission("sys:dict:delete")
    public ResultBean<?> deleteType(@PathVariable Long id) {
        dictTypeService.deleteDictType(id);
        return ResultBean.success();
    }

    // ---- 字典项 ----

    @GetMapping("/item/list")
    @SaCheckPermission("sys:dict:query")
    public ResultBean<List<DictItemVO>> itemList(@RequestParam String dictType) {
        return ResultBean.success(dictItemService.listByDictType(dictType));
    }

    @PostMapping("/item")
    @SaCheckPermission("sys:dict:create")
    public ResultBean<?> addItem(@RequestBody @Validated AddDictItemDTO dto) {
        dictItemService.addDictItem(dto);
        return ResultBean.success();
    }

    @PutMapping("/item")
    @SaCheckPermission("sys:dict:update")
    public ResultBean<?> editItem(@RequestBody @Validated EditDictItemDTO dto) {
        dictItemService.editDictItem(dto);
        return ResultBean.success();
    }

    @DeleteMapping("/item/{id}")
    @SaCheckPermission("sys:dict:delete")
    public ResultBean<?> deleteItem(@PathVariable Long id) {
        dictItemService.deleteDictItem(id);
        return ResultBean.success();
    }
}
