package com.example.demospringsecurity.service;

import com.example.demospringsecurity.dto.request.CommentRequest;
import com.example.demospringsecurity.exceptions.FriendNotFoundException;
import com.example.demospringsecurity.exceptions.NotPermissionException;
import com.example.demospringsecurity.exceptions.PostNotFoundException;
import com.example.demospringsecurity.exceptions.UserNotFoundException;
import com.example.demospringsecurity.model.Comment;
import com.example.demospringsecurity.model.UserInfo;
import com.example.demospringsecurity.model.UserPost;
import com.example.demospringsecurity.repository.CommentRepository;
import com.example.demospringsecurity.repository.UserInfoRepository;
import com.example.demospringsecurity.repository.UserPostRepository;
import com.example.demospringsecurity.response.comment.CommentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class CommentService {
    @Autowired
    CommentRepository commentRepository;

    @Autowired
    UserPostRepository userPostRepository;

    @Autowired
    FriendService friendService;

    @Autowired
    UserInfoRepository userInfoRepository;

    public CommentResponse postComment(CommentRequest commentRequest) {
        CommentResponse commentResponse = new CommentResponse();
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            UserInfo userInfo = userInfoRepository.findByUserName(currentUserName)
                    .orElseThrow(() -> new UserNotFoundException("Not found user!"));
            commentRequest.setCommentUserId(userInfo.getUserId());
        } else return commentResponse;
        if (!userPostRepository.existsById(commentRequest.getCommentPostId())) {
            throw new PostNotFoundException("Not found post has postId: " + commentRequest.getCommentPostId());
        }
        //        check friend
        String currentUserName = friendService.getListFriends()
                .getCurrentUserName();
        List<String> listUserNameCanSeePost = friendService.getListFriends()
                .getUserNameFriends();
        listUserNameCanSeePost.add(currentUserName);
        UserPost userPost = userPostRepository.findUserPostByPostIdAndAndPostDeleteFlag(commentRequest.getCommentPostId(),
                        0)
                .orElseThrow(() -> new PostNotFoundException("Post not exist!"));
        UserInfo userPostedThePost = userInfoRepository.findByUserId(userPost.getPostUserId())
                .orElseThrow(() -> new UserNotFoundException("Not found user!"));
        if (!listUserNameCanSeePost.contains(userPostedThePost.getUserName())) {
            throw new FriendNotFoundException("You can not comment the post because you and " + userPostedThePost.getUserName() + " are not friend!");
        }
        if (commentRequest.getCommentId() != 0) {
            return editComment(commentRequest);
        } else {
            Comment comment = new Comment();
            LocalDateTime dateCreateComment = LocalDateTime.now();
            comment.setCommentCreateDate(dateCreateComment);
            comment.setCommentPostId(commentRequest.getCommentPostId());
            comment.setCommentContent(commentRequest.getCommentContent());
            comment.setCommentUserId(commentRequest.getCommentUserId());
            Comment commentPost = commentRepository.save(comment);
            commentResponse.setComment(commentPost);
            commentResponse.setStatus("200");
            commentResponse.setMessage("Post comment success!");
            return commentResponse;
        }
    }

    public CommentResponse editComment(CommentRequest commentRequest) {
        CommentResponse commentResponse = new CommentResponse();
        Comment comment = new Comment();
        comment.setCommentId(commentRequest.getCommentId());
        Comment commentCheck = commentRepository.findById(commentRequest.getCommentId())
                .get();
        if (commentCheck.getCommentUserId() == commentRequest.getCommentUserId()) {
            comment.setCommentCreateDate(commentCheck.getCommentCreateDate());
            comment.setCommentPostId(commentCheck.getCommentPostId());
            comment.setCommentContent(commentRequest.getCommentContent());
            comment.setCommentUserId(commentRequest.getCommentUserId());
            Comment commentPost = commentRepository.save(comment);
            commentResponse.setComment(commentPost);
            commentResponse.setStatus("200");
            commentResponse.setMessage("Post comment success!");
        } else {
            throw new NotPermissionException("You do not have permission to edit this comment!");
        }
        return commentResponse;
    }

    public CommentResponse deleteComment(int commentId) {
        CommentResponse commentResponse = new CommentResponse();
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            UserInfo userInfo = userInfoRepository.findByUserName(currentUserName)
                    .orElseThrow(() -> new UserNotFoundException("Not found user!"));
            Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new RuntimeException("Comment not exists!"));
            if (userInfo.getUserId() == comment.getCommentUserId()) {
                commentRepository.deleteById(commentId);
                commentResponse.setStatus("200");
                commentResponse.setMessage("Delete comment success!");
                commentResponse.setComment(comment);
            } else {
                throw new NotPermissionException("You do not have permission to delete this comment!");
            }
        }
        return commentResponse;
    }


}
