package com.example.demospringsecurity.service;

import com.example.demospringsecurity.dto.request.CommentRequest;
import com.example.demospringsecurity.exceptions.PostNotFoundException;
import com.example.demospringsecurity.model.Comment;
import com.example.demospringsecurity.model.UserInfo;
import com.example.demospringsecurity.model.UserPost;
import com.example.demospringsecurity.repository.CommentRepository;
import com.example.demospringsecurity.repository.UserInfoRepository;
import com.example.demospringsecurity.repository.UserPostRepository;
import com.example.demospringsecurity.response.comment.CommentResponse;
import com.example.demospringsecurity.response.friend.GetListFriendResponse;
import com.example.demospringsecurity.response.user.ChangeInfoUserResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    @InjectMocks
    CommentService commentService;

    @Mock
    CommentRepository commentRepository;

    @Mock
    UserPostRepository userPostRepository;

    @Mock
    FriendService friendService;

    @Mock
    UserInfoRepository userInfoRepository;


    @Test
    void postComment_accessDenied() {
        CommentRequest commentRequest = new CommentRequest();
        Authentication authentication = Mockito.mock(AnonymousAuthenticationToken.class);
// Mockito.whens() for your authorization object
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        CommentResponse commentResponse = commentService.postComment(commentRequest);
        Assertions.assertNull(commentResponse.getMessage());
    }

    @Test
    void postComment_postNotFoundException() {
        CommentRequest commentRequest = new CommentRequest();
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(1);
        Authentication authentication = Mockito.mock(Authentication.class);
// Mockito.whens() for your authorization object
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(authentication.getName()).thenReturn("oisadoiasuda");
        Mockito.when(userInfoRepository.findByUserName(Mockito.anyString())).thenReturn(Optional.of(userInfo));
        Mockito.when(userInfoRepository.existsById(Mockito.anyInt())).thenReturn(false);
        Assertions.assertThrows(PostNotFoundException.class, () -> commentService.postComment(commentRequest));
    }

    @Test
    void postComment_postNotFoundException_postNotExists() {
        CommentRequest commentRequest = new CommentRequest();
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(1);
        Authentication authentication = Mockito.mock(Authentication.class);
// Mockito.whens() for your authorization object
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(authentication.getName()).thenReturn("oisadoiasuda");
        Mockito.when(userInfoRepository.findByUserName(Mockito.anyString())).thenReturn(Optional.of(userInfo));
        Mockito.when(userInfoRepository.existsById(Mockito.anyInt())).thenReturn(true);
        GetListFriendResponse getListFriendResponse = new GetListFriendResponse();
        List<String> userNameFriends = new ArrayList<>();
        getListFriendResponse.setCurrentUserName("askjdnaskdkjas");
        getListFriendResponse.setUserNameFriends(userNameFriends);
        Mockito.when(friendService.getListFriends()).thenReturn(getListFriendResponse);
        Mockito.when(userPostRepository.findUserPostByPostIdAndAndPostDeleteFlag(Mockito.anyInt(),
                Mockito.anyInt())).thenThrow(PostNotFoundException.class);
        Assertions.assertThrows(PostNotFoundException.class, () -> commentService.postComment(commentRequest));
    }

    @Test
    void postComment_canNotComment() {
        CommentRequest commentRequest = new CommentRequest();
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(1);
        Authentication authentication = Mockito.mock(Authentication.class);
// Mockito.whens() for your authorization object
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(authentication.getName()).thenReturn("oisadoiasuda");
        Mockito.when(userInfoRepository.findByUserName(Mockito.anyString())).thenReturn(Optional.of(userInfo));
        Mockito.when(userInfoRepository.existsById(Mockito.anyInt())).thenReturn(true);
        GetListFriendResponse getListFriendResponse = new GetListFriendResponse();
        List<String> userNameFriends = new ArrayList<>();
        getListFriendResponse.setCurrentUserName("askjdnaskdkjas");
        getListFriendResponse.setUserNameFriends(userNameFriends);
        Mockito.when(friendService.getListFriends()).thenReturn(getListFriendResponse);
        UserPost userPost = new UserPost();
        Mockito.when(userPostRepository.findUserPostByPostIdAndAndPostDeleteFlag(Mockito.anyInt(),
                Mockito.anyInt())).thenReturn(Optional.of(userPost));
        Mockito.when(userInfoRepository.findByUserId(Mockito.anyInt())).thenReturn(Optional.of(userInfo));
        CommentResponse commentResponse = commentService.postComment(commentRequest);
        Assertions.assertNull(commentResponse.getComment());
    }

    @Test
    void postComment_success() {
        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setCommentPostId(23);
        commentRequest.setCommentContent("sadnlsadl");
        commentRequest.setCommentUserId(1);
        UserInfo userInfo = new UserInfo();
        userInfo.setUserName("abc");
        userInfo.setUserId(1);
        Authentication authentication = Mockito.mock(Authentication.class);
// Mockito.whens() for your authorization object
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(authentication.getName()).thenReturn("oisadoiasuda");
        Mockito.when(userInfoRepository.findByUserName(Mockito.anyString())).thenReturn(Optional.of(userInfo));
        Mockito.when(userInfoRepository.existsById(Mockito.anyInt())).thenReturn(true);
        GetListFriendResponse getListFriendResponse = new GetListFriendResponse();
        List<String> userNameFriends = new ArrayList<>();
        userNameFriends.add("abc");
        getListFriendResponse.setCurrentUserName("askjdnaskdkjas");
        getListFriendResponse.setUserNameFriends(userNameFriends);
        Mockito.when(friendService.getListFriends()).thenReturn(getListFriendResponse);
        UserPost userPost = new UserPost();
        Mockito.when(userPostRepository.findUserPostByPostIdAndAndPostDeleteFlag(Mockito.anyInt(),
                Mockito.anyInt())).thenReturn(Optional.of(userPost));
        Mockito.when(userInfoRepository.findByUserId(Mockito.anyInt())).thenReturn(Optional.of(userInfo));
        Comment comment = new Comment();
        Mockito.when(commentRepository.save(Mockito.any())).thenReturn(comment);
        CommentResponse commentResponse = commentService.postComment(commentRequest);
        Assertions.assertNotNull(commentResponse.getComment());
        Assertions.assertEquals("Post comment success!", commentResponse.getMessage());
//        Assertions.assertNull(commentResponse.getComment());
    }

    @Test
    void postComment_editSuccess() {
        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setCommentPostId(23);
        commentRequest.setCommentContent("sadnlsadl");
        commentRequest.setCommentUserId(1);
        commentRequest.setCommentId(1);
        UserInfo userInfo = new UserInfo();
        userInfo.setUserName("abc");
        userInfo.setUserId(1);
        Authentication authentication = Mockito.mock(Authentication.class);
// Mockito.whens() for your authorization object
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(authentication.getName()).thenReturn("oisadoiasuda");
        Mockito.when(userInfoRepository.findByUserName(Mockito.anyString())).thenReturn(Optional.of(userInfo));
        Mockito.when(userInfoRepository.existsById(Mockito.anyInt())).thenReturn(true);
        GetListFriendResponse getListFriendResponse = new GetListFriendResponse();
        List<String> userNameFriends = new ArrayList<>();
        userNameFriends.add("abc");
        getListFriendResponse.setCurrentUserName("askjdnaskdkjas");
        getListFriendResponse.setUserNameFriends(userNameFriends);
        Mockito.when(friendService.getListFriends()).thenReturn(getListFriendResponse);
        UserPost userPost = new UserPost();
        Mockito.when(userPostRepository.findUserPostByPostIdAndAndPostDeleteFlag(Mockito.anyInt(),
                Mockito.anyInt())).thenReturn(Optional.of(userPost));
        Mockito.when(userInfoRepository.findByUserId(Mockito.anyInt())).thenReturn(Optional.of(userInfo));
        Optional<Comment> comment = Optional.of(new Comment());
        comment.get().setCommentUserId(1);
        Mockito.when(commentRepository.findById(commentRequest.getCommentId())).thenReturn(comment);
        Mockito.when(commentRepository.save(Mockito.any())).thenReturn(comment.get());
        CommentResponse commentResponse = commentService.postComment(commentRequest);
        Assertions.assertNotNull(commentResponse.getComment());
        Assertions.assertEquals("Post comment success!", commentResponse.getMessage());
    }

    @Test
    void postComment_editFail() {
        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setCommentPostId(23);
        commentRequest.setCommentContent("sadnlsadl");
        commentRequest.setCommentUserId(1);
        commentRequest.setCommentId(1);
        UserInfo userInfo = new UserInfo();
        userInfo.setUserName("abc");
        userInfo.setUserId(1);
        Authentication authentication = Mockito.mock(Authentication.class);
// Mockito.whens() for your authorization object
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(authentication.getName()).thenReturn("oisadoiasuda");
        Mockito.when(userInfoRepository.findByUserName(Mockito.anyString())).thenReturn(Optional.of(userInfo));
        Mockito.when(userInfoRepository.existsById(Mockito.anyInt())).thenReturn(true);
        GetListFriendResponse getListFriendResponse = new GetListFriendResponse();
        List<String> userNameFriends = new ArrayList<>();
        userNameFriends.add("abc");
        getListFriendResponse.setCurrentUserName("askjdnaskdkjas");
        getListFriendResponse.setUserNameFriends(userNameFriends);
        Mockito.when(friendService.getListFriends()).thenReturn(getListFriendResponse);
        UserPost userPost = new UserPost();
        Mockito.when(userPostRepository.findUserPostByPostIdAndAndPostDeleteFlag(Mockito.anyInt(),
                Mockito.anyInt())).thenReturn(Optional.of(userPost));
        Mockito.when(userInfoRepository.findByUserId(Mockito.anyInt())).thenReturn(Optional.of(userInfo));
        Optional<Comment> comment = Optional.of(new Comment());
        comment.get().setCommentUserId(2);
        Mockito.when(commentRepository.findById(commentRequest.getCommentId())).thenReturn(comment);
        CommentResponse commentResponse = commentService.postComment(commentRequest);
        Assertions.assertNull(commentResponse.getComment());
        Assertions.assertEquals("You do not have permission to edit this comment!", commentResponse.getMessage());
    }

    @Test
    void deleteComment_accessDenied() {
        Authentication authentication = Mockito.mock(AnonymousAuthenticationToken.class);
// Mockito.whens() for your authorization object
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        CommentResponse commentResponse = commentService.deleteComment(1);
        Assertions.assertNull(commentResponse.getMessage());
    }

    @Test
    void deleteComment_success() {
        Authentication authentication = Mockito.mock(Authentication.class);
// Mockito.whens() for your authorization object
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Optional<UserInfo> userInfo = Optional.of(new UserInfo());
        userInfo.get().setUserId(1);
        Mockito.when(authentication.getName()).thenReturn("oasidjoasod");
        Mockito.when(userInfoRepository.findByUserName(Mockito.anyString())).thenReturn(userInfo);
        Optional<Comment> comment = Optional.of(new Comment());
        comment.get().setCommentUserId(1);
        Mockito.when(commentRepository.findById(Mockito.anyInt())).thenReturn(comment);

        CommentResponse commentResponse = commentService.deleteComment(1);
        Assertions.assertEquals("Delete comment success!",commentResponse.getMessage());
        Assertions.assertNotNull(commentResponse.getComment());
    }

    @Test
    void deleteComment_fail() {
        Authentication authentication = Mockito.mock(Authentication.class);
// Mockito.whens() for your authorization object
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Optional<UserInfo> userInfo = Optional.of(new UserInfo());
        userInfo.get().setUserId(1);
        Mockito.when(authentication.getName()).thenReturn("oasidjoasod");
        Mockito.when(userInfoRepository.findByUserName(Mockito.anyString())).thenReturn(userInfo);
        Optional<Comment> comment = Optional.of(new Comment());
        comment.get().setCommentUserId(2);
        Mockito.when(commentRepository.findById(Mockito.anyInt())).thenReturn(comment);
        CommentResponse commentResponse = commentService.deleteComment(2);
        Assertions.assertEquals("You do not have permission to delete this comment!",commentResponse.getMessage());
        Assertions.assertNotNull(commentResponse.getComment());
    }


}