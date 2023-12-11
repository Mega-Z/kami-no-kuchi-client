package com.megaz.knk.exception;

public class BuffParamNotFilledException extends RuntimeException{
    public BuffParamNotFilledException(String param) {
        super("Buff参数："+param + "未确定");
    }
}
