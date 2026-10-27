package com.jzone.module.system.dict.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jzone.common.pojo.PageResult;
import com.jzone.module.system.dict.pojo.dto.*;
import com.jzone.module.system.dict.pojo.po.DictTypePO;
import com.jzone.module.system.dict.pojo.vo.DictTypeVO;

public interface DictTypeService extends IService<DictTypePO> {

    PageResult<DictTypeVO> pageDictType(QueryDictTypeDTO dto);

    void addDictType(AddDictTypeDTO dto);

    void editDictType(EditDictTypeDTO dto);

    void deleteDictType(Long id);
}
