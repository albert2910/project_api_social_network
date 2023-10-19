package com.example.demospringsecurity.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "TBFriend")
public class Friend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "id_user_receiver")
    private int userReceiverId;

    @Column(name = "id_user_sender")
    private int userSenderId;

    //  status = 0 unfriend
    //  status = 1 sending
    //  status = 2 accept
    @Column(name = "status")
    private int status;

    @Column(name = "friend_time_create")
    private LocalDateTime friendCreateDate;
}
