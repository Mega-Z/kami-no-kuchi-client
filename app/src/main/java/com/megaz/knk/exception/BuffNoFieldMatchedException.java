package com.megaz.knk.exception;

public class BuffNoFieldMatchedException extends RuntimeException {
    public BuffNoFieldMatchedException(String buffName) {
        super("Buff："+buffName+"无匹配乘区");
    }
}
