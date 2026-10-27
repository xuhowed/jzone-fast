package com.jzone.framework.result;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResultBean<T> {

    private Integer code;
    private String msg;
    private T data;

    private ResultBean(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> ResultBean<T> success() {
        return new ResultBean<>(200, "success", null);
    }

    public static <T> ResultBean<T> success(T data) {
        return new ResultBean<>(200, "success", data);
    }

    public static <T> ResultBean<T> fail(String msg) {
        return new ResultBean<>(400, msg, null);
    }

    public static <T> ResultBean<T> fail(Integer code, String msg) {
        return new ResultBean<>(code, msg, null);
    }

    public static <T> ResultBean<T> error(String msg) {
        return new ResultBean<>(500, msg, null);
    }
}
