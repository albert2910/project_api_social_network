package com.example.demospringsecurity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserViewDto {
    private int userId;

    private String username;

    private String userFullName;

    private String userAvatar;

    private String userEmail;

    private Date userBirthDate;

    private String userAddress;
}
