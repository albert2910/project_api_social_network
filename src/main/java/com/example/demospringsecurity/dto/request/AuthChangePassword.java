package com.example.demospringsecurity.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthChangePassword {
    private String newPassword;
    private String reNewPassword;
    private String tokenReset;
}
