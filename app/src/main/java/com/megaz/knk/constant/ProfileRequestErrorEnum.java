package com.megaz.knk.constant;

import lombok.Getter;

@Getter
public enum ProfileRequestErrorEnum {
    TIMEOUT("网络错误，重试超限"),
    UID_NOT_FOUND("uid不存在，米哈游说的"),
    SERVER_ERROR("服务器内部错误"),
    DATASOURCE_ERROR("数据源访问错误"),
    PROFILE_NOT_AVAILABLE("无法获取面板");


    private final String message;

    ProfileRequestErrorEnum(String message) {
        this.message = message;
    }
}
