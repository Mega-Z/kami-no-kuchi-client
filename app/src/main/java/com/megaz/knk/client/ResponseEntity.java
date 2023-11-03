package com.megaz.knk.client;

import lombok.Getter;

@Getter
public class ResponseEntity<T> {
    private final int code;
    private final T body;

    public ResponseEntity(int code) {
        this.code = code;
        this.body = null;
    }

    public ResponseEntity(int code, T body) {
        this.code = code;
        this.body = body;
    }


}
