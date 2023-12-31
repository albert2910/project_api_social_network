package com.example.demospringsecurity.response.auth;

import com.example.demospringsecurity.dto.RegisterUserSuccess;
import com.example.demospringsecurity.dto.UserDto;
import com.example.demospringsecurity.model.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterResponse {
    private String message;
    private RegisterUserSuccess data;
}
