package com.example.demospringsecurity.dto;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private int userId;

    private String userName;

    private String userFullName;

    private String userAvatar;

    private String userEmail;

    private String roles;

    private String userOtp;

    private LocalDateTime userTimeCreateOtp;

    private Date userBirthDate;

    private String userAddress;
}
