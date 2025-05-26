package com.sweetfun.response;

import lombok.Data;

@Data
public class Result<T> {
    private int code;
    private T data;
    private String desc;

    public Result() {}

    public Result(int code, T data, String desc) {
        this.code = code;
        this.data = data;
        this.desc = desc;
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(200, data, "操作成功");
    }

    public static <T> Result<T> success(T data, String desc) {
        return new Result<>(200, data, desc);
    }

    public static <T> Result<T> error(int code, String desc) {
        return new Result<>(code, null, desc);
    }

    public static <T> Result<T> error(String desc) {
        return new Result<>(500, null, desc);
    }
}

