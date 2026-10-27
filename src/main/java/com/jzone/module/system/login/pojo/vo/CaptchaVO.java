package com.jzone.module.system.login.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaptchaVO {

    /** 验证码唯一标识 */
    private String token;

    /** 是否开启了验证码 */
    private Boolean enabled;

    /** 背景图 Base64（带凹槽） */
    private String backgroundImage;

    /** 滑块图 Base64 */
    private String sliderImage;

    /** 滑块凹槽的 Y 轴坐标 */
    private Integer y;
}
