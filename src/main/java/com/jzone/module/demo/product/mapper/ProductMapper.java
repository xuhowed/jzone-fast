package com.jzone.module.demo.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jzone.module.demo.product.pojo.po.ProductPO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductMapper extends BaseMapper<ProductPO> {
}
