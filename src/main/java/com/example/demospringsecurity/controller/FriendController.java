package com.example.demospringsecurity.controller;

import com.example.demospringsecurity.dto.request.AddFriendRequest;
import com.example.demospringsecurity.response.FriendResponse;
import com.example.demospringsecurity.response.GetFriendRequestsResponse;
import com.example.demospringsecurity.response.GetListFriendResponse;
import com.example.demospringsecurity.service.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class FriendController {
    @Autowired
    FriendService friendService;

    @PostMapping("/add-friend/{userReceiverId}")
    public FriendResponse addFriend(@PathVariable int userReceiverId, @RequestBody AddFriendRequest addFriendRequest) {
        addFriendRequest.setUserReceiverId(userReceiverId);
        return friendService.addFriend(addFriendRequest);
    }

    @PostMapping("/accept-friend/{userSenderId}")
    public FriendResponse acceptFriend(@PathVariable int userSenderId, @RequestBody AddFriendRequest addFriendRequest) {
        addFriendRequest.setUserSenderId(userSenderId);
        return friendService.acceptFriend(addFriendRequest);
    }

    @GetMapping("/friendRequests")
    public GetFriendRequestsResponse getAllFriendRequests() {
        return friendService.getAllFriendRequestsByCurrentUser();
    }

    @GetMapping("/list-friends")
    public GetListFriendResponse getListFriend() {
        return friendService.getListFriends();
    }
}
