package com.example.demospringsecurity.response.friend;

import com.example.demospringsecurity.dto.FriendDto;
import com.example.demospringsecurity.model.Friend;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetFriendRequestsResponse {
    private String message;
    private List<FriendDto> friendRequests;
}
