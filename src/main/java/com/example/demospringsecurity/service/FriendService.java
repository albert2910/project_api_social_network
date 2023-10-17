package com.example.demospringsecurity.service;

import com.example.demospringsecurity.dto.request.FriendRequest;
import com.example.demospringsecurity.model.Friend;
import com.example.demospringsecurity.model.UserInfo;
import com.example.demospringsecurity.repository.FriendRepository;
import com.example.demospringsecurity.repository.UserInfoRepository;
import com.example.demospringsecurity.response.FriendResponse;
import com.example.demospringsecurity.response.GetFriendRequestsResponse;
import com.example.demospringsecurity.response.GetListFriendResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class FriendService {
    @Autowired
    FriendRepository friendRepository;
    @Autowired
    UserInfoRepository userInfoRepository;

    //  sent, unsent friend request
    public FriendResponse addFriend(FriendRequest friendRequest) {
        FriendResponse addFriendResponse = new FriendResponse();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            UserInfo currentUser = userInfoRepository.findByUserName(currentUserName).get();
            friendRequest.setUserSenderId(currentUser.getUserId());
        }
        if (!userInfoRepository.existsUserInfoByUserId(friendRequest.getUserReceiverId())) {
            addFriendResponse.setStatus("400");
            addFriendResponse.setMessage("Not found receiver!");
            addFriendResponse.setFriend(null);
            return addFriendResponse;
        }
        //  status = 0 unfriend
        //  status = 1 sending
        //  status = 2 accept
        boolean checkFriend = friendRepository.existsFriendByUserReceiverIdAndAndUserSenderId(friendRequest.getUserReceiverId(),
                friendRequest.getUserSenderId());

        boolean checkFriendRequest = friendRepository.existsFriendByUserReceiverIdAndAndUserSenderIdAndAndStatus(friendRequest.getUserSenderId(),
                friendRequest.getUserReceiverId(),
                1);
//      nếu check đã có lời mời kết bạn từ bên kia thì thông báo là cần accept, không phải gửi lại lời mời kết bạn
        if (checkFriendRequest) {
            addFriendResponse.setStatus("400");
            addFriendResponse.setMessage("User " + userInfoRepository.findByUserId(friendRequest.getUserReceiverId()).get().getUserName() + " sent a friend request to you! Please check list friend requests and accept!");
            addFriendResponse.setFriend(null);
            return addFriendResponse;
        }
        if (checkFriend) {
            Friend friend = friendRepository.findFriendByUserReceiverIdAndAndUserSenderId(friendRequest.getUserReceiverId(),
                    friendRequest.getUserSenderId());
            if (friend.getStatus() == 0) {
                friend.setStatus(1);
                friendRepository.save(friend);
                addFriendResponse.setStatus("200");
                addFriendResponse.setMessage("Sent a friend request!");
                addFriendResponse.setFriend(friend);
                return addFriendResponse;
            }
            if (friend.getStatus() == 1) {
                friend.setStatus(0);
                friendRepository.save(friend);
                addFriendResponse.setStatus("200");
                addFriendResponse.setMessage("Unsent a friend request!");
                addFriendResponse.setFriend(friend);
                return addFriendResponse;
            }
            if (friend.getStatus() == 2) {
                addFriendResponse.setStatus("400");
                addFriendResponse.setMessage("Already friends!");
                addFriendResponse.setFriend(friend);
                return addFriendResponse;
            }
        } else {
            Friend friend = new Friend();
            friend.setUserSenderId(friendRequest.getUserSenderId());
            friend.setUserReceiverId(friendRequest.getUserReceiverId());
            friend.setStatus(1);
            friendRepository.save(friend);
            addFriendResponse.setStatus("200");
            addFriendResponse.setMessage("Sent a friend request!");
            addFriendResponse.setFriend(friend);
        }
        return addFriendResponse;
    }

    public FriendResponse acceptFriend(FriendRequest friendRequest) {
        FriendResponse acceptFriendResponse = new FriendResponse();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            UserInfo currentUser = userInfoRepository.findByUserName(currentUserName).get();
            friendRequest.setUserReceiverId(currentUser.getUserId());
        }
        Friend friend = friendRepository.findFriendByUserReceiverIdAndAndUserSenderIdAndAndStatus(friendRequest.getUserReceiverId(),
                friendRequest.getUserSenderId(),
                1);
        if (friend != null) {
            friend.setStatus(2);
            friendRepository.save(friend);
            acceptFriendResponse.setStatus("200");
            UserInfo userSender = userInfoRepository.findById(friendRequest.getUserSenderId()).get();
            acceptFriendResponse.setMessage("Accept friend " + userSender.getUserName() + "!");
            acceptFriendResponse.setFriend(friend);
//          check TH nguoc lai, neu co ton tai => delete 
            Friend friendCheck = friendRepository.findFriendByUserReceiverIdAndAndUserSenderIdAndAndStatus(friendRequest.getUserSenderId(),
                    friendRequest.getUserReceiverId(), 0);
            if(friendCheck != null) {
                friendRepository.delete(friendCheck);
            }
        } else {
            acceptFriendResponse.setStatus("400");
            acceptFriendResponse.setMessage("No invitation!");
            acceptFriendResponse.setFriend(null);
        }
        return acceptFriendResponse;
    }

    public GetFriendRequestsResponse getAllFriendRequestsByCurrentUser() {
        GetFriendRequestsResponse getFriendRequestsResponse = new GetFriendRequestsResponse();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            UserInfo currentUser = userInfoRepository.findByUserName(currentUserName).get();
            List<Friend> friendRequests = friendRepository.findFriendByUserReceiverIdAndAndStatus(currentUser.getUserId(),
                    1);
            getFriendRequestsResponse.setStatus("200");
            getFriendRequestsResponse.setMessage("Get all friend requests successfully!");
            getFriendRequestsResponse.setFriendRequests(friendRequests);
        } else {
            getFriendRequestsResponse.setStatus("401");
            getFriendRequestsResponse.setMessage("Token is invalid!");
            getFriendRequestsResponse.setFriendRequests(null);
        }
        return getFriendRequestsResponse;
    }

    public GetListFriendResponse getListFriends() {
        GetListFriendResponse getListFriendResponse = new GetListFriendResponse();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            UserInfo currentUser = userInfoRepository.findByUserName(currentUserName).get();
            List<Friend> friends = friendRepository.findListFriendByCurrentUserId(currentUser.getUserId());
            List<String> userNameFriends = new ArrayList<>();
            Set<Integer> idFriends = new LinkedHashSet<>();
            for (Friend friend : friends) {
                if (friend.getUserSenderId() != currentUser.getUserId()) {
                    idFriends.add(friend.getUserSenderId());
                }
                if (friend.getUserReceiverId() != currentUser.getUserId()) {
                    idFriends.add(friend.getUserReceiverId());
                }
            }
            for (Integer idFriend : idFriends) {
                userNameFriends.add(userInfoRepository.findById(idFriend).get().getUserName());
            }
            getListFriendResponse.setStatus("200");
            getListFriendResponse.setMessage("Get your list friend successfully!");
            getListFriendResponse.setUserNameFriends(userNameFriends);
        } else {
            getListFriendResponse.setStatus("401");
            getListFriendResponse.setMessage("Token is invalid!");
            getListFriendResponse.setUserNameFriends(null);
        }
        return getListFriendResponse;
    }

    public FriendResponse unFriend(FriendRequest friendRequest) {
        FriendResponse unFriendResponse = new FriendResponse();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            UserInfo currentUser = userInfoRepository.findByUserName(currentUserName).get();
            friendRequest.setUserSenderId(currentUser.getUserId());
        }
        Friend friend = friendRepository.findFriendByUserReceiverIdAndAndUserSenderId(friendRequest.getUserReceiverId(),
                friendRequest.getUserSenderId());
        if(friend.getStatus() == 0) {
            unFriendResponse.setStatus("400");
            unFriendResponse.setMessage("Are not friend! Can not unfriend!");
            unFriendResponse.setFriend(friend);
            return unFriendResponse;
        }
        return unFriendResponse;
    }


}
