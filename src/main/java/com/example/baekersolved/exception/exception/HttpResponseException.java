package com.example.baekersolved.exception.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class HttpResponseException extends RuntimeException{
    public HttpResponseException(String msg) {
        super(msg);
    }
}
