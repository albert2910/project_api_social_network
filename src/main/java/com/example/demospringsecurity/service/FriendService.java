package com.example.demospringsecurity.service;

import com.example.demospringsecurity.dto.FriendDto;
import com.example.demospringsecurity.dto.request.FriendRequest;
import com.example.demospringsecurity.exceptions.BadRequestException;
import com.example.demospringsecurity.exceptions.UserNotFoundException;
import com.example.demospringsecurity.mapper.FriendMapper;
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
    @Autowired
    FriendMapper friendMapper;

    //  sent, unsent friend request
//  if A sent B, B sent A => friend
    public FriendResponse addFriend(String usernameReceiver) {
        FriendRequest friendRequest = new FriendRequest();
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            UserInfo currentUser = userInfoRepository.findByUserName(currentUserName)
                    .orElseThrow(() -> new UserNotFoundException("Not found user!"));
            friendRequest.setUserSenderId(currentUser.getUserId());
        }
        UserInfo userReceiver = userInfoRepository.findByUserName(usernameReceiver)
                .orElseThrow(() -> new UserNotFoundException("Not found receiver!"));
        friendRequest.setUserReceiverId(userReceiver.getUserId());
        FriendResponse addFriendResponse = new FriendResponse();

//        gui loi moi ket ban cho chinh minh
        if (friendRequest.getUserReceiverId() == friendRequest.getUserSenderId()) {
            throw new BadRequestException("You can not send friend request to yourself!");
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
            Friend friend = friendRepository.findFriendByUserReceiverIdAndAndUserSenderIdAndAndStatus(friendRequest.getUserSenderId(),
                    friendRequest.getUserReceiverId(),
                    1);
            friend.setStatus(2);
            Friend friendSaved = friendRepository.save(friend);
            FriendDto friendDto = friendMapper.toDto(friendSaved);
            addFriendResponse.setMessage("Accept user " + userReceiver.getUserName() + " success!");
            addFriendResponse.setFriend(friendDto);
            return addFriendResponse;
        }
//       nếu check đã la bạn từ bên kia thì thông báo là da la ban, không phải gửi lại lời mời kết bạn
        if (checkAlreadyFriend) {
            throw new BadRequestException("User " + usernameReceiver + " and you already friend!");
        }
        if (checkFriend) {
            Friend friend = friendRepository.findFriendByUserReceiverIdAndAndUserSenderId(friendRequest.getUserReceiverId(),
                    friendRequest.getUserSenderId());
            if (friend.getStatus() == 0) {
                friend.setStatus(1);
                LocalDateTime currentDate = LocalDateTime.now();
                friend.setFriendCreateDate(currentDate);
                Friend friendSaved = friendRepository.save(friend);
                FriendDto friendDto = friendMapper.toDto(friendSaved);
                addFriendResponse.setMessage("Sent a friend request!");
                addFriendResponse.setFriend(friendDto);
                return addFriendResponse;
            }
            if (friend.getStatus() == 1) {
                friend.setStatus(0);
                LocalDateTime currentDate = LocalDateTime.now();
                friend.setFriendCreateDate(currentDate);
                Friend friendSaved = friendRepository.save(friend);
                FriendDto friendDto = friendMapper.toDto(friendSaved);
                addFriendResponse.setMessage("Unsent a friend request!");
                addFriendResponse.setFriend(friendDto);
                return addFriendResponse;
            }
            if (friend.getStatus() == 2) {
                throw new BadRequestException("Already friends!");
            }
        } else if (checkNoFriend) {
            Friend friend = friendRepository.findFriendByUserReceiverIdAndAndUserSenderId(friendRequest.getUserSenderId(),
                    friendRequest.getUserReceiverId());
            friend.setUserReceiverId(friendRequest.getUserReceiverId());
            friend.setUserSenderId(friendRequest.getUserSenderId());
            friend.setStatus(1);
            LocalDateTime currentDate = LocalDateTime.now();
            friend.setFriendCreateDate(currentDate);
            Friend friendSaved = friendRepository.save(friend);
            FriendDto friendDto = friendMapper.toDto(friendSaved);
            addFriendResponse.setMessage("Sent a friend request!");
            addFriendResponse.setFriend(friendDto);
            return addFriendResponse;
        } else {
            Friend friend = new Friend();
            friend.setUserSenderId(friendRequest.getUserSenderId());
            friend.setUserReceiverId(friendRequest.getUserReceiverId());
            friend.setStatus(1);
            LocalDateTime currentDate = LocalDateTime.now();
            friend.setFriendCreateDate(currentDate);
            Friend friendSaved = friendRepository.save(friend);
            FriendDto friendDto = friendMapper.toDto(friendSaved);
            addFriendResponse.setMessage("Sent a friend request!");
            addFriendResponse.setFriend(friendDto);
        }
        return addFriendResponse;
    }

    public FriendResponse acceptFriend(String usernameSender) {
        FriendRequest friendRequest = new FriendRequest();
        UserInfo userSender = userInfoRepository.findByUserName(usernameSender)
                .orElseThrow(() -> new UserNotFoundException("Not found user has username: " + usernameSender));
        friendRequest.setUserSenderId(userSender.getUserId());
        FriendResponse acceptFriendResponse = new FriendResponse();
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            UserInfo currentUser = userInfoRepository.findByUserName(currentUserName)
                    .orElseThrow(() -> new UserNotFoundException("Not found user!"));
            friendRequest.setUserReceiverId(currentUser.getUserId());
        }
//      accept chinh ban than minh
        if (friendRequest.getUserSenderId() == friendRequest.getUserReceiverId()) {
            throw new BadRequestException("You can not accept yourself!");
        }
        Friend friend = friendRepository.findFriendByUserReceiverIdAndAndUserSenderIdAndAndStatus(friendRequest.getUserReceiverId(),
                friendRequest.getUserSenderId(),
                1);
        if (friend != null) {
            LocalDateTime currentDate = LocalDateTime.now();
            friend.setFriendCreateDate(currentDate);
            friend.setStatus(2);
            Friend friendSaved = friendRepository.save(friend);
            FriendDto friendDto = friendMapper.toDto(friendSaved);
            acceptFriendResponse.setMessage("Accept friend " + userSender.getUserName() + "!");
            acceptFriendResponse.setFriend(friendDto);
//          check TH nguoc lai, neu co ton tai => delete
            Friend friendCheck = friendRepository.findFriendByUserReceiverIdAndAndUserSenderIdAndAndStatus(friendRequest.getUserSenderId(),
                    friendRequest.getUserReceiverId(),
                    0);
            if (friendCheck != null) {
                friendRepository.delete(friendCheck);
            }
        } else {
            throw new BadRequestException("No invitation!");
        }
        return acceptFriendResponse;
    }

    public GetFriendRequestsResponse getAllFriendRequestsByCurrentUser() {
        GetFriendRequestsResponse getFriendRequestsResponse = new GetFriendRequestsResponse();
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            UserInfo currentUser = userInfoRepository.findByUserName(currentUserName)
                    .get();
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
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            getListFriendResponse.setCurrentUserName(currentUserName);
            UserInfo currentUser = userInfoRepository.findByUserName(currentUserName)
                    .get();
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
                userNameFriends.add(userInfoRepository.findById(idFriend)
                        .get()
                        .getUserName());
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

    public FriendResponse unFriend(String usernameReceiver) {
        FriendRequest friendRequest = new FriendRequest();
        UserInfo userInfo = userInfoRepository.findByUserName(usernameReceiver)
                .orElseThrow(() -> new UserNotFoundException("Not found user has username: " + usernameReceiver));
        friendRequest.setUserReceiverId(userInfo.getUserId());
        FriendResponse unFriendResponse = new FriendResponse();
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            UserInfo currentUser = userInfoRepository.findByUserName(currentUserName)
                    .orElseThrow(() -> new UserNotFoundException("Not found user has username: " + currentUserName));
            friendRequest.setUserSenderId(currentUser.getUserId());
        }

//        unfriend ban than minh
        if (userInfo.getUserId() == friendRequest.getUserSenderId()) {
            throw new BadRequestException("You can not unfriend yourself!");
        }

        Friend friend = friendRepository.findFriendByUserReceiverIdAndAndUserSenderId(friendRequest.getUserReceiverId(),
                friendRequest.getUserSenderId());
        Friend friend1 = friendRepository.findFriendByUserReceiverIdAndAndUserSenderId(friendRequest.getUserSenderId(),
                friendRequest.getUserReceiverId());

        if (friend == null && friend1 == null) {
            throw new BadRequestException("Are not friend! Can not unfriend!");
        }
//      cả hai chưa là bạn
        if (friend.getStatus() == 0) {
            throw new BadRequestException("Are not friend! Can not unfriend!");

        }
//      đang gửi lời mời kết bạn
        if (friend.getStatus() == 1) {
            throw new BadRequestException("Are not friend! Can not unfriend! You can check list friend requests and decline!");
        }
//      cả hai đã là bạn
        if (friend.getStatus() == 2) {
            friend.setStatus(0);
            Friend friendSaved = friendRepository.save(friend);
            FriendDto friendDto = friendMapper.toDto(friendSaved);
            unFriendResponse.setMessage("Unfriend is successful! You must have had difficulty making a decision..");
            unFriendResponse.setFriend(friendDto);
            return unFriendResponse;
        }
        return unFriendResponse;
    }


}
