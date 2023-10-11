package com.example.demospringsecurity.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TBPassword_reset_token")
public class PasswordResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prt_id")
    private int passwordResetTokenId;

    @Column(name = "prt_token")
    private String tokenReset;

    @Column(name = "prt_user_id")
    private int userId;

    @Column(name = "prt_date_token")
    private LocalDateTime passwordResetToken_datetime;

}
