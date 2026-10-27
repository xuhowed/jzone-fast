package com.jzone.module.system.dict.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jzone.module.system.dict.pojo.dto.*;
import com.jzone.module.system.dict.pojo.po.DictItemPO;
import com.jzone.module.system.dict.pojo.vo.DictItemVO;

import java.util.List;

public interface DictItemService extends IService<DictItemPO> {

    List<DictItemVO> listByDictType(String dictType);

    void addDictItem(AddDictItemDTO dto);

    void editDictItem(EditDictItemDTO dto);

    void deleteDictItem(Long id);
}
