package com.jzone.module.system.login.service;

import com.jzone.module.system.login.pojo.vo.CaptchaVO;

public interface CaptchaService {

    /**
     * 生成滑块验证码
     */
    CaptchaVO generateCaptcha();

    /**
     * 校验滑块验证码
     */
    void verifyCaptcha(String token, Integer userX);
}
