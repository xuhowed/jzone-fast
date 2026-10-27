package com.jzone.framework.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final Integer code;

    public BusinessException(String msg) {
        super(msg);
        this.code = 400;
    }

    public BusinessException(Integer code, String msg) {
        super(msg);
        this.code = code;
    }
}
