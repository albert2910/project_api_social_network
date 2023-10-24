package com.example.demospringsecurity.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeInfoUserRequest {
    private int userId;
    private String userName;
    private String userFullName;
    private String userAvatar;
    private String userEmail;
    private Date userBirthDate;
    private String userAddress;

}
