package com.jzone.module.system.user.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordDTO {

    private Long id;

    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 32, message = "密码长度6-32位")
    private String newPassword;
}
