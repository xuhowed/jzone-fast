package com.jzone.module.system.login.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import com.jzone.common.constant.CacheKeyConstants;
import com.jzone.framework.cache.CacheService;
import com.jzone.framework.exception.BusinessException;
import com.jzone.framework.result.ResultBean;
import com.jzone.framework.web.JZoneSecurityProperties;
import com.jzone.module.system.login.pojo.dto.LoginDTO;
import com.jzone.module.system.login.pojo.vo.CaptchaVO;
import com.jzone.module.system.login.pojo.vo.LoginVO;
import com.jzone.module.system.login.service.CaptchaService;
import com.jzone.module.system.user.mapper.UserRoleMapper;
import com.jzone.module.system.user.pojo.po.UserPO;
import com.jzone.module.system.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.List;

@RestController
@RequestMapping("/sys")
@RequiredArgsConstructor
public class LoginController {

    private final UserService userService;
    private final UserRoleMapper userRoleMapper;
    private final PasswordEncoder passwordEncoder;
    private final CaptchaService captchaService;
    private final JZoneSecurityProperties securityProperties;
    private final CacheService cacheService;

    @SaIgnore
    @GetMapping("/captcha/slider")
    public ResultBean<CaptchaVO> getCaptcha() {
        return ResultBean.success(captchaService.generateCaptcha());
    }

    @SaIgnore
    @PostMapping("/login")
    public ResultBean<LoginVO> login(@RequestBody @Validated LoginDTO dto) {
        String lockKey = CacheKeyConstants.loginLock(dto.getUsername());
        Long lockedUntil = cacheService.get(lockKey, Long.class);
        if (lockedUntil != null) {
            long diffMs = lockedUntil - System.currentTimeMillis();
            if (diffMs > 0) {
                long remainingMinutes = Math.max(1, (diffMs + 59999) / 60000);
                throw new BusinessException("账号因密码连续错误已被锁定，请在 " + remainingMinutes + " 分钟后重试");
            } else {
                cacheService.evict(lockKey);
            }
        }

        // 校验滑块验证码（如果开关开启）
        if (securityProperties.isCaptchaEnabled()) {
            captchaService.verifyCaptcha(dto.getCaptchaToken(), dto.getCaptchaX());
        }

        UserPO user = userService.getByUsername(dto.getUsername());
        if (user == null || !passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            String errKey = CacheKeyConstants.loginErrorCount(dto.getUsername());
            Integer errCount = cacheService.get(errKey, Integer.class);
            if (errCount == null) {
                errCount = 0;
            }
            errCount++;
            if (errCount >= securityProperties.getMaxRetryCount()) {
                long lockDurationSeconds = securityProperties.getLockDuration();
                long lockedUntilMs = System.currentTimeMillis() + lockDurationSeconds * 1000L;
                cacheService.put(lockKey, lockedUntilMs, Duration.ofSeconds(lockDurationSeconds));
                cacheService.evict(errKey);
                throw new BusinessException("密码错误次数已达 " + securityProperties.getMaxRetryCount() + " 次，账号已锁定 " + (lockDurationSeconds / 60) + " 分钟");
            } else {
                cacheService.put(errKey, errCount, Duration.ofSeconds(securityProperties.getLockDuration()));
                int remaining = securityProperties.getMaxRetryCount() - errCount;
                throw new BusinessException("用户名或密码错误，还可尝试 " + remaining + " 次");
            }
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用");
        }

        // 登录成功，清除重试错误计数与锁定状态
        cacheService.evict(lockKey);
        cacheService.evict(CacheKeyConstants.loginErrorCount(dto.getUsername()));

        // 内置超级管理员 admin 兜底自愈角色绑定
        if ("admin".equalsIgnoreCase(user.getUsername())) {
            List<Long> rIds = userRoleMapper.selectRoleIdsByUserId(user.getId());
            if (CollUtil.isEmpty(rIds)) {
                userRoleMapper.insertBatch(user.getId(), List.of(1L));
            }
        }

        // 统一使用 userId 作为 Sa-Token 登录 ID，并在 Session 中存储 username
        StpUtil.login(user.getId());
        StpUtil.getSession().set("username", user.getUsername());
        String token = StpUtil.getTokenValue();

        // 登录时清除该用户旧的菜单路由与权限缓存，确保获取最新权限
        cacheService.evict(CacheKeyConstants.menuTreeByUser(user.getId()));
        cacheService.evict(CacheKeyConstants.menuPermsByUser(user.getId()));

        LoginVO vo = new LoginVO();
        vo.setToken(token);
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setRoles(userService.getRoleCodesByUserId(user.getId()));
        vo.setPermissions(userService.getPermissionsByUserId(user.getId()));
        return ResultBean.success(vo);
    }

    @PostMapping("/logout")
    public ResultBean<?> logout() {
        try {
            Long userId = StpUtil.getLoginIdAsLong();
            cacheService.evict(CacheKeyConstants.menuTreeByUser(userId));
            cacheService.evict(CacheKeyConstants.menuPermsByUser(userId));
        } catch (Exception ignored) {
        }
        StpUtil.logout();
        return ResultBean.success();
    }
}
