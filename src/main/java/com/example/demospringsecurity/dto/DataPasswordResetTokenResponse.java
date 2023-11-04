package com.example.demospringsecurity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DataPasswordResetTokenResponse {
    private String email;
    private String tokenReset;
}
