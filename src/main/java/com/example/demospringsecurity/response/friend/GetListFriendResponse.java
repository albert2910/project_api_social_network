package com.example.demospringsecurity.response.friend;

import com.example.demospringsecurity.model.Friend;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetListFriendResponse {
    private String message;
    private String currentUserName;
    private List<String> userNameFriends;
}
