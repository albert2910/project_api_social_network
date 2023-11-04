package com.example.demospringsecurity.response.user;

import com.example.demospringsecurity.dto.UserViewDto;
import com.example.demospringsecurity.model.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeInfoUserResponse {
    private String status;
    private String message;
    private UserViewDto data;
}
