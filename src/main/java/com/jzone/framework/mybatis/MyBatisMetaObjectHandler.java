package com.jzone.framework.mybatis;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MyBatisMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, now);
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, now);
        this.strictInsertFill(metaObject, "createBy", String.class, currentUsername());
        this.strictInsertFill(metaObject, "updateBy", String.class, currentUsername());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        this.strictUpdateFill(metaObject, "updateBy", String.class, currentUsername());
    }

    private String currentUsername() {
        try {
            if (StpUtil.isLogin()) {
                Object username = StpUtil.getSession().get("username");
                if (username != null && StrUtil.isNotBlank(username.toString())) {
                    return username.toString();
                }
                return StpUtil.getLoginIdAsString();
            }
            return "system";
        } catch (Exception e) {
            return "system";
        }
    }
}
