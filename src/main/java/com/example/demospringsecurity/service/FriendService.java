package com.example.demospringsecurity.service;

import com.example.demospringsecurity.dto.request.FriendRequest;
import com.example.demospringsecurity.model.Friend;
import com.example.demospringsecurity.model.UserInfo;
import com.example.demospringsecurity.repository.FriendRepository;
import com.example.demospringsecurity.repository.UserInfoRepository;
import com.example.demospringsecurity.response.friend.FriendResponse;
import com.example.demospringsecurity.response.friend.GetFriendRequestsResponse;
import com.example.demospringsecurity.response.friend.GetListFriendResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    public FriendResponse addFriend(int userReceiverId) {
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setUserReceiverId(userReceiverId);
        FriendResponse addFriendResponse = new FriendResponse();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            UserInfo currentUser = userInfoRepository.findByUserName(currentUserName).get();
            friendRequest.setUserSenderId(currentUser.getUserId());
        }
//      gui loi moi ket ban den user ko ton tai
        if (!userInfoRepository.existsUserInfoByUserId(friendRequest.getUserReceiverId())) {
            addFriendResponse.setStatus("400");
            addFriendResponse.setMessage("Not found receiver!");
            addFriendResponse.setFriend(null);
            return addFriendResponse;
        }
//        gui loi moi ket ban cho chinh minh
        if (friendRequest.getUserReceiverId() == friendRequest.getUserSenderId()) {
            addFriendResponse.setStatus("400");
            addFriendResponse.setMessage("You can not send friend request to yourself!");
            addFriendResponse.setFriend(null);
            return addFriendResponse;
        }
        //  status = 0 unfriend
        //  status = 1 sending
        //  status = 2 accept
        boolean checkFriend = friendRepository.existsFriendByUserReceiverIdAndAndUserSenderId(friendRequest.getUserReceiverId(),
                friendRequest.getUserSenderId());
        boolean checkNoFriend = friendRepository.existsFriendByUserReceiverIdAndAndUserSenderIdAndAndStatus(friendRequest.getUserSenderId(),
                friendRequest.getUserReceiverId(),
                0);
        boolean checkFriendRequest = friendRepository.existsFriendByUserReceiverIdAndAndUserSenderIdAndAndStatus(friendRequest.getUserSenderId(),
                friendRequest.getUserReceiverId(),
                1);
        boolean checkAlreadyFriend = friendRepository.existsFriendByUserReceiverIdAndAndUserSenderIdAndAndStatus(friendRequest.getUserSenderId(),
                friendRequest.getUserReceiverId(),
                2);

//      nếu check đã có lời mời kết bạn từ bên kia thì thông báo là cần accept, không phải gửi lại lời mời kết bạn
        if (checkFriendRequest) {
            addFriendResponse.setStatus("400");
            addFriendResponse.setMessage("User " + userInfoRepository.findByUserId(friendRequest.getUserReceiverId()).get().getUserName() + " sent a friend request to you! Please check list friend requests and accept!");
            addFriendResponse.setFriend(null);
            return addFriendResponse;
        }
//       nếu check đã la bạn từ bên kia thì thông báo là da la ban, không phải gửi lại lời mời kết bạn
        if (checkAlreadyFriend) {
            addFriendResponse.setStatus("400");
            addFriendResponse.setMessage("User " + userInfoRepository.findByUserId(friendRequest.getUserReceiverId()).get().getUserName() + " and you already friend!");
            addFriendResponse.setFriend(null);
            return addFriendResponse;
        }
        if (checkFriend) {
            Friend friend = friendRepository.findFriendByUserReceiverIdAndAndUserSenderId(friendRequest.getUserReceiverId(),
                    friendRequest.getUserSenderId());
            if (friend.getStatus() == 0) {
                friend.setStatus(1);
                LocalDateTime currentDate = LocalDateTime.now();
                friend.setFriendCreateDate(currentDate);
                friendRepository.save(friend);
                addFriendResponse.setStatus("200");
                addFriendResponse.setMessage("Sent a friend request!");
                addFriendResponse.setFriend(friend);
                return addFriendResponse;
            }
            if (friend.getStatus() == 1) {
                friend.setStatus(0);
                LocalDateTime currentDate = LocalDateTime.now();
                friend.setFriendCreateDate(currentDate);
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
        } else if (checkNoFriend) {
            Friend friend = friendRepository.findFriendByUserReceiverIdAndAndUserSenderId(friendRequest.getUserSenderId(),
                    friendRequest.getUserReceiverId());
            friend.setUserReceiverId(friendRequest.getUserReceiverId());
            friend.setUserSenderId(friendRequest.getUserSenderId());
            friend.setStatus(1);
            LocalDateTime currentDate = LocalDateTime.now();
            friend.setFriendCreateDate(currentDate);
            friendRepository.save(friend);
            addFriendResponse.setStatus("200");
            addFriendResponse.setMessage("Sent a friend request!");
            addFriendResponse.setFriend(friend);
            return addFriendResponse;
        } else {
            Friend friend = new Friend();
            friend.setUserSenderId(friendRequest.getUserSenderId());
            friend.setUserReceiverId(friendRequest.getUserReceiverId());
            friend.setStatus(1);
            LocalDateTime currentDate = LocalDateTime.now();
            friend.setFriendCreateDate(currentDate);
            friendRepository.save(friend);
            addFriendResponse.setStatus("200");
            addFriendResponse.setMessage("Sent a friend request!");
            addFriendResponse.setFriend(friend);
        }
        return addFriendResponse;
    }

    public FriendResponse acceptFriend(int userSenderId) {
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setUserSenderId(userSenderId);
        FriendResponse acceptFriendResponse = new FriendResponse();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            UserInfo currentUser = userInfoRepository.findByUserName(currentUserName).get();
            friendRequest.setUserReceiverId(currentUser.getUserId());
        }
//      accept 1 user ko ton tai
        if (!userInfoRepository.existsUserInfoByUserId(friendRequest.getUserSenderId())) {
            acceptFriendResponse.setStatus("400");
            acceptFriendResponse.setMessage("Not found sender!");
            acceptFriendResponse.setFriend(null);
            return acceptFriendResponse;
        }
//      accept chinh ban than minh
        if (friendRequest.getUserSenderId() == friendRequest.getUserReceiverId()) {
            acceptFriendResponse.setStatus("400");
            acceptFriendResponse.setMessage("You can not accept yourself!");
            acceptFriendResponse.setFriend(null);
            return acceptFriendResponse;
        }
        Friend friend = friendRepository.findFriendByUserReceiverIdAndAndUserSenderIdAndAndStatus(friendRequest.getUserReceiverId(),
                friendRequest.getUserSenderId(),
                1);
        if (friend != null) {
            LocalDateTime currentDate = LocalDateTime.now();
            friend.setFriendCreateDate(currentDate);
            friend.setStatus(2);
            friendRepository.save(friend);
            acceptFriendResponse.setStatus("200");
            UserInfo userSender = userInfoRepository.findById(friendRequest.getUserSenderId()).get();
            acceptFriendResponse.setMessage("Accept friend " + userSender.getUserName() + "!");
            acceptFriendResponse.setFriend(friend);
//          check TH nguoc lai, neu co ton tai => delete
            Friend friendCheck = friendRepository.findFriendByUserReceiverIdAndAndUserSenderIdAndAndStatus(friendRequest.getUserSenderId(),
                    friendRequest.getUserReceiverId(),
                    0);
            if (friendCheck != null) {
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
            getListFriendResponse.setCurrentUserName(currentUserName);
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

    public FriendResponse unFriend(int userReceiverId) {
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setUserReceiverId(userReceiverId);
        FriendResponse unFriendResponse = new FriendResponse();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            UserInfo currentUser = userInfoRepository.findByUserName(currentUserName).get();
            friendRequest.setUserSenderId(currentUser.getUserId());
        }
//        unfriend 1 user ko ton tai
        if (!userInfoRepository.existsUserInfoByUserId(friendRequest.getUserReceiverId())) {
            unFriendResponse.setStatus("400");
            unFriendResponse.setMessage("Not found user!");
            unFriendResponse.setFriend(null);
            return unFriendResponse;
        }
//        unfriend ban than minh
        if(userReceiverId == friendRequest.getUserSenderId()) {
            unFriendResponse.setStatus("400");
            unFriendResponse.setMessage("You can not unfriend yourself!");
            unFriendResponse.setFriend(null);
            return unFriendResponse;
        }
        Friend friend = friendRepository.findFriendByUserReceiverIdAndAndUserSenderId(friendRequest.getUserReceiverId(),
                friendRequest.getUserSenderId());
        if (friend == null) {
            friend = friendRepository.findFriendByUserReceiverIdAndAndUserSenderId(friendRequest.getUserSenderId(),
                    friendRequest.getUserReceiverId());
            if (friend == null) {
                unFriendResponse.setStatus("400");
                unFriendResponse.setMessage("Are not friend! Can not unfriend!");
                unFriendResponse.setFriend(null);
                return unFriendResponse;
            }
        }
//      cả hai chưa là bạn
        if (friend.getStatus() == 0) {
            unFriendResponse.setStatus("400");
            unFriendResponse.setMessage("Are not friend! Can not unfriend!");
            unFriendResponse.setFriend(null);
            return unFriendResponse;
        }
//      đang gửi lời mời kết bạn
        if (friend.getStatus() == 1) {
            unFriendResponse.setStatus("400");
            unFriendResponse.setMessage("Are not friend! Can not unfriend! You can check list friend requests and decline!");
            unFriendResponse.setFriend(null);
            return unFriendResponse;
        }
//      cả hai đã là bạn
        if (friend.getStatus() == 2) {
            friend.setStatus(0);
            unFriendResponse.setStatus("200");
            unFriendResponse.setMessage("Unfriend is successful! You must have had difficulty making a decision..");
            unFriendResponse.setFriend(friend);
            return unFriendResponse;
        }
        return unFriendResponse;
    }


}
