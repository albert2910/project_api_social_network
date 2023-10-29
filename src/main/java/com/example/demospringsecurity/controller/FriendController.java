package com.example.demospringsecurity.controller;

import com.example.demospringsecurity.response.friend.FriendResponse;
import com.example.demospringsecurity.response.friend.GetFriendRequestsResponse;
import com.example.demospringsecurity.response.friend.GetListFriendResponse;
import com.example.demospringsecurity.service.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/friends")
public class FriendController {
    @Autowired
    FriendService friendService;

    @PostMapping("/{userReceiverId}/add-friend")
    public FriendResponse addFriend(@PathVariable int userReceiverId) {
        return friendService.addFriend(userReceiverId);
    }

    @PostMapping("/{userSenderId}/accept-friend")
    public FriendResponse acceptFriend(@PathVariable int userSenderId) {
        return friendService.acceptFriend(userSenderId);
    }

    @PostMapping("/{userReceiverId}/unfriend")
    public FriendResponse unFriend(@PathVariable int userReceiverId) {
        return friendService.unFriend(userReceiverId);
    }

    @GetMapping("/requests")
    public GetFriendRequestsResponse getAllFriendRequests() {
        return friendService.getAllFriendRequestsByCurrentUser();
    }

    @GetMapping("/my-friends")
    public GetListFriendResponse getListFriend() {
        return friendService.getListFriends();
    }
}
