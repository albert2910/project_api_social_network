package com.example.demospringsecurity.response;

import com.example.demospringsecurity.model.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterResponse {
    private String message;
    private boolean isSuccess;
    private UserInfo userInfo;
}
