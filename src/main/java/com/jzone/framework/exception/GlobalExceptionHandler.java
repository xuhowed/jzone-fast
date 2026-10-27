package com.jzone.framework.exception;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import com.jzone.framework.result.ResultBean;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResultBean<?> handleBusinessException(BusinessException e) {
        return ResultBean.fail(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultBean<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .orElse("参数校验失败");
        return ResultBean.fail(400, msg);
    }

    @ExceptionHandler(BindException.class)
    public ResultBean<?> handleBindException(BindException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .orElse("参数校验失败");
        return ResultBean.fail(400, msg);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResultBean<?> handleConstraintViolationException(ConstraintViolationException e) {
        String msg = e.getConstraintViolations().stream()
                .findFirst()
                .map(cv -> cv.getMessage())
                .orElse("参数校验失败");
        return ResultBean.fail(400, msg);
    }

    @ExceptionHandler(NotLoginException.class)
    public ResultBean<?> handleNotLoginException(NotLoginException e) {
        return ResultBean.fail(401, "未登录或登录已过期");
    }

    @ExceptionHandler({NotPermissionException.class, NotRoleException.class})
    public ResultBean<?> handleNotPermissionException(Exception e) {
        return ResultBean.fail(403, "无权限访问");
    }

    @ExceptionHandler(Exception.class)
    public ResultBean<?> handleException(Exception e) {
        log.error("系统异常", e);
        return ResultBean.error("系统繁忙，请稍后重试");
    }
}
