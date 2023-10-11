package com.example.demospringsecurity.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String status;
    private String userName;
    private String otp;
    private String message;
}
