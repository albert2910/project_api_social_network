package com.example.demospringsecurity.response.auth;

import com.example.demospringsecurity.dto.DataPasswordResetTokenResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetTokenResponse {
    private String message;
    private DataPasswordResetTokenResponse data;
}
