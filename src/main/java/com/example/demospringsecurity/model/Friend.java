package com.example.demospringsecurity.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Friend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "id_user_receiver")
    private int userReceiverId;

    @Column(name = "id_user_sender")
    private int userSenderId;

    @Column(name = "status")
//  status = 0 sending
//  status = 1 accept
//  status = 2 decline
    private int status;
}
