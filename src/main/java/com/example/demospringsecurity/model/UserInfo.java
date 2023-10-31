package com.example.demospringsecurity.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "TBUser")
public class UserInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int userId;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "user_full_name")
    private String userFullName;

    @Column(name = "user_avatar")
    private String userAvatar;

    @Column(name = "user_email")
    private String userEmail;

    @Column(name = "user_password")
    @JsonIgnore
    private String userPassword;

    @Column(name = "user_role")
    private String roles;

    @Column(name = "user_otp")
    private String userOtp;

    @Column(name = "user_time_create_otp")
    private LocalDateTime userTimeCreateOtp;

    @Column(name = "user_birthdate")
    private Date userBirthDate;

    @Column(name = "user_address")
    private String userAddress;

}
