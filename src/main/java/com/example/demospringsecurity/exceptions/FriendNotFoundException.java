package com.example.demospringsecurity.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class FriendNotFoundException extends RuntimeException{
    public FriendNotFoundException(String message) {
        super(message);
    }
}
