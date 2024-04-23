package com.megaz.knk.exception;

public class ProfileQueryException extends RuntimeException {
    public ProfileQueryException(String message) {
        super("数据查询失败：" + message);
    }
}
