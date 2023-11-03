package com.megaz.knk.exception;

public class RequestErrorException extends RuntimeException{
    public RequestErrorException(String message) {
        super(message);
    }
}
