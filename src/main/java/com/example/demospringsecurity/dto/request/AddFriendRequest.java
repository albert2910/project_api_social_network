package com.example.demospringsecurity.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddFriendRequest {
    private int id;
    private int userReceiverId;
    private int userSenderId;
    //  status = 0 unfriend
    //  status = 1 sending
    //  status = 2 accept
//    private int status;
}
