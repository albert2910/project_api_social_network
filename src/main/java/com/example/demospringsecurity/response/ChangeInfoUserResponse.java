package com.example.demospringsecurity.response;

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
    private UserInfo userInfoUpdate;
}
