package com.jzone.module.system.role.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddRoleDTO {

    @NotBlank(message = "角色名称不能为空")
    private String roleName;

    @NotBlank(message = "角色编码不能为空")
    private String roleCode;

    private Integer sort = 0;
    private Integer status = 1;
    private String remark;
}
