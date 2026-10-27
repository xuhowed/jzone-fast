package com.jzone.framework.web;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "jzone.security")
public class JZoneSecurityProperties {

    /** 是否开启滑块验证码 */
    private boolean captchaEnabled = true;

    /** 密码最大重试次数（达到后锁定） */
    private int maxRetryCount = 5;

    /** 锁定时间（秒），默认 600 秒（10分钟） */
    private int lockDuration = 600;
}
