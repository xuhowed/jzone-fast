package com.jzone.module.system.role.pojo.dto;

import com.jzone.common.pojo.PageDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class QueryRoleDTO extends PageDTO {

    private String roleName;
    private String roleCode;
    private Integer status;
}
