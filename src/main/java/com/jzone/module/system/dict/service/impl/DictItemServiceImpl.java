package com.jzone.module.system.dict.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jzone.common.constant.CacheKeyConstants;
import com.jzone.framework.cache.CacheService;
import com.jzone.framework.exception.BusinessException;
import com.jzone.module.system.dict.mapper.DictItemMapper;
import com.jzone.module.system.dict.mapper.DictTypeMapper;
import com.jzone.module.system.dict.pojo.dto.*;
import com.jzone.module.system.dict.pojo.po.DictItemPO;
import com.jzone.module.system.dict.pojo.po.DictTypePO;
import com.jzone.module.system.dict.pojo.vo.DictItemVO;
import com.jzone.module.system.dict.service.DictItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DictItemServiceImpl extends ServiceImpl<DictItemMapper, DictItemPO>
        implements DictItemService {

    private final DictTypeMapper dictTypeMapper;
    private final CacheService cacheService;

    @Override
    public List<DictItemVO> listByDictType(String dictType) {
        String key = CacheKeyConstants.dictItems(dictType);
        return cacheService.getOrLoad(key, List.class, () -> {
            List<DictItemPO> items = this.lambdaQuery()
                    .eq(DictItemPO::getDictType, dictType)
                    .eq(DictItemPO::getStatus, 1)
                    .orderByAsc(DictItemPO::getSort)
                    .list();
            return BeanUtil.copyToList(items, DictItemVO.class);
        });
    }

    @Override
    public void addDictItem(AddDictItemDTO dto) {
        // 校验字典类型是否存在
        boolean typeExists = dictTypeMapper.selectCount(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<DictTypePO>()
                        .eq(DictTypePO::getDictType, dto.getDictType())
        ) > 0;
        if (!typeExists) {
            throw new BusinessException("字典类型不存在: " + dto.getDictType());
        }

        // 校验同类型下字典值是否已存在
        boolean itemExists = this.lambdaQuery()
                .eq(DictItemPO::getDictType, dto.getDictType())
                .eq(DictItemPO::getItemValue, dto.getItemValue())
                .exists();
        if (itemExists) {
            throw new BusinessException("字典值已存在: " + dto.getItemValue());
        }

        this.save(BeanUtil.copyProperties(dto, DictItemPO.class));
        cacheService.evict(CacheKeyConstants.dictItems(dto.getDictType()));
    }

    @Override
    public void editDictItem(EditDictItemDTO dto) {
        DictItemPO po = this.getById(dto.getId());
        if (po == null) {
            throw new BusinessException("字典项不存在");
        }

        if (StrUtil.isNotBlank(dto.getItemValue()) && !dto.getItemValue().equals(po.getItemValue())) {
            boolean itemExists = this.lambdaQuery()
                    .eq(DictItemPO::getDictType, po.getDictType())
                    .eq(DictItemPO::getItemValue, dto.getItemValue())
                    .ne(DictItemPO::getId, dto.getId())
                    .exists();
            if (itemExists) {
                throw new BusinessException("字典值已存在: " + dto.getItemValue());
            }
        }

        BeanUtil.copyProperties(dto, po, "id", "dictType");
        this.updateById(po);
        cacheService.evict(CacheKeyConstants.dictItems(po.getDictType()));
    }

    @Override
    public void deleteDictItem(Long id) {
        DictItemPO po = this.getById(id);
        if (po == null) {
            throw new BusinessException("字典项不存在");
        }
        this.removeById(id);
        cacheService.evict(CacheKeyConstants.dictItems(po.getDictType()));
    }
}
