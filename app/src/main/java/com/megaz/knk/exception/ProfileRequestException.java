package com.megaz.knk.exception;

import com.megaz.knk.constant.ProfileRequestErrorEnum;

import lombok.Getter;

@Getter
public class ProfileRequestException extends RuntimeException{
    private ProfileRequestErrorEnum type;
    public ProfileRequestException(String message) {
        super(message);
    }
    public ProfileRequestException(ProfileRequestErrorEnum type) {
        super(type.getMessage());
        this.type = type;
    }
}
