package com.example.demospringsecurity.response;

import com.example.demospringsecurity.model.Friend;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetFriendRequestsResponse {
    private String status;
    private String message;
    private List<Friend> friendRequests;
}
