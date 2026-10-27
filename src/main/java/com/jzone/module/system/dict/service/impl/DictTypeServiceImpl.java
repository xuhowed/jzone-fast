package com.jzone.module.system.dict.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jzone.common.constant.CacheKeyConstants;
import com.jzone.common.pojo.PageResult;
import com.jzone.framework.cache.CacheService;
import com.jzone.framework.exception.BusinessException;
import com.jzone.module.system.dict.mapper.DictTypeMapper;
import com.jzone.module.system.dict.pojo.dto.*;
import com.jzone.module.system.dict.pojo.po.DictTypePO;
import com.jzone.module.system.dict.pojo.vo.DictTypeVO;
import com.jzone.module.system.dict.service.DictItemService;
import com.jzone.module.system.dict.service.DictTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DictTypeServiceImpl extends ServiceImpl<DictTypeMapper, DictTypePO>
        implements DictTypeService {

    private final DictItemService dictItemService;
    private final CacheService cacheService;

    @Override
    public PageResult<DictTypeVO> pageDictType(QueryDictTypeDTO dto) {
        Page<DictTypePO> page = this.lambdaQuery()
                .like(StrUtil.isNotBlank(dto.getDictName()), DictTypePO::getDictName, dto.getDictName())
                .like(StrUtil.isNotBlank(dto.getDictType()), DictTypePO::getDictType, dto.getDictType())
                .eq(dto.getStatus() != null, DictTypePO::getStatus, dto.getStatus())
                .page(dto.toPage());
        return PageResult.of(page, BeanUtil.copyToList(page.getRecords(), DictTypeVO.class));
    }

    @Override
    public void addDictType(AddDictTypeDTO dto) {
        boolean exists = this.lambdaQuery().eq(DictTypePO::getDictType, dto.getDictType()).exists();
        if (exists) {
            throw new BusinessException("字典类型已存在");
        }
        this.save(BeanUtil.copyProperties(dto, DictTypePO.class));
    }

    @Override
    public void editDictType(EditDictTypeDTO dto) {
        DictTypePO po = this.getById(dto.getId());
        if (po == null) {
            throw new BusinessException("字典类型不存在");
        }
        // 排除 id 与 dictType，禁止修改字典类型编码防止字典项脱钩
        BeanUtil.copyProperties(dto, po, "id", "dictType");
        this.updateById(po);
        cacheService.evict(CacheKeyConstants.dictItems(po.getDictType()));
    }

    @Override
    public void deleteDictType(Long id) {
        DictTypePO po = this.getById(id);
        if (po == null) {
            throw new BusinessException("字典类型不存在");
        }
        boolean hasItems = dictItemService.lambdaQuery()
                .eq(com.jzone.module.system.dict.pojo.po.DictItemPO::getDictType, po.getDictType())
                .exists();
        if (hasItems) {
            throw new BusinessException("该字典类型下存在字典项，无法删除");
        }
        this.removeById(id);
        cacheService.evict(CacheKeyConstants.dictItems(po.getDictType()));
    }
}
