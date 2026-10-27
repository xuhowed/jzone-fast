package com.jzone.module.system.user.pojo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EditUserDTO {

    @NotNull(message = "用户ID不能为空")
    private Long id;

    private String nickname;
    private String email;
    private String phone;
    private Integer status;
}
