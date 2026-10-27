package com.jzone.module.system.login.pojo.vo;

import lombok.Data;

import java.util.List;

@Data
public class LoginVO {

    private String token;
    private Long userId;
    private String username;
    private String nickname;
    private List<String> roles;
    private List<String> permissions;
}
