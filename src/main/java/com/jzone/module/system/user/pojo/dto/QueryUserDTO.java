package com.jzone.module.system.user.pojo.dto;

import com.jzone.common.pojo.PageDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class QueryUserDTO extends PageDTO {

    private String username;
    private String nickname;
    private Integer status;
}
