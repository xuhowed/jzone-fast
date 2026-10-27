package com.jzone.module.system.login.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginDTO {

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    /** 滑块验证码凭证 */
    private String captchaToken;

    /** 滑块移动横坐标 */
    private Integer captchaX;
}
