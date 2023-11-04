package com.example.demospringsecurity.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.GONE)
public class ExpiredException extends RuntimeException{
    public ExpiredException(String message) {
        super(message);
    }
}
