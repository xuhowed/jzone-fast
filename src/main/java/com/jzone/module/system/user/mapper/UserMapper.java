package com.jzone.module.system.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jzone.module.system.user.pojo.po.UserPO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<UserPO> {
}
