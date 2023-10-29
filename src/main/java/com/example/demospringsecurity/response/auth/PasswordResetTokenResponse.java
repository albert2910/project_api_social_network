package com.example.demospringsecurity.response.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetTokenResponse {
    private String status;
    private String message;
    private String resetToken;
}