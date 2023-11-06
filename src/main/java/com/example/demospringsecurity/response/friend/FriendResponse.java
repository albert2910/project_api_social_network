package com.example.demospringsecurity.response.friend;

import com.example.demospringsecurity.dto.FriendDto;
import com.example.demospringsecurity.model.Friend;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendResponse {
    private String message;
    private FriendDto friend;
}
