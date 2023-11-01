package com.example.demospringsecurity.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NotPermissionException extends RuntimeException{
    public NotPermissionException(String message) {
        super(message);
    }
}
