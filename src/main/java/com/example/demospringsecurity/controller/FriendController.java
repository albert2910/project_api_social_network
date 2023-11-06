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

    @PostMapping("/{usernameReceiver}/add-friend")
    public FriendResponse addFriend(@RequestParam String usernameReceiver) {
        return friendService.addFriend(usernameReceiver);
    }

    @PostMapping("/{usernameSender}/accept-friend")
    public FriendResponse acceptFriend(@RequestParam String usernameSender) {
        return friendService.acceptFriend(usernameSender);
    }

    @PostMapping("/{usernameReceiver}/unfriend")
    public FriendResponse unFriend(@RequestParam String usernameReceiver) {
        return friendService.unFriend(usernameReceiver);
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
