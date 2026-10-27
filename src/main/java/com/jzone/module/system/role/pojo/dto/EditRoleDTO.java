package com.jzone.module.system.role.pojo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EditRoleDTO {

    @NotNull(message = "角色ID不能为空")
    private Long id;

    private String roleName;
    private String roleCode;
    private Integer sort;
    private Integer status;
    private String remark;
}
