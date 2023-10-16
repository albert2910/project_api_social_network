package com.example.demospringsecurity.service;

import com.example.demospringsecurity.dto.request.CommentRequest;
import com.example.demospringsecurity.model.Comment;
import com.example.demospringsecurity.model.UserInfo;
import com.example.demospringsecurity.repository.CommentRepository;
import com.example.demospringsecurity.repository.UserInfoRepository;
import com.example.demospringsecurity.response.CommentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CommentService {
    @Autowired
    CommentRepository commentRepository;

    @Autowired
    UserInfoRepository userInfoRepository;

    public CommentResponse postComment(CommentRequest commentRequest) {
        CommentResponse commentResponse = new CommentResponse();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            UserInfo userInfo = userInfoRepository.findByUserName(currentUserName).get();
            commentRequest.setCommentUserId(userInfo.getUserId());
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
        Comment commentCheck = commentRepository.findById(commentRequest.getCommentId()).get();
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
            commentResponse.setComment(null);
            commentResponse.setStatus("400");
            commentResponse.setMessage("You do not have permission to edit this comment!");
        }
        return commentResponse;
    }

    public CommentResponse deleteComment(int commentId) {
        CommentResponse commentResponse = new CommentResponse();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            UserInfo userInfo = userInfoRepository.findByUserName(currentUserName).get();
            Comment comment = commentRepository.findById(commentId).get();
            if(userInfo.getUserId() == comment.getCommentUserId()) {
                commentRepository.deleteById(commentId);
                commentResponse.setStatus("200");
                commentResponse.setMessage("Delete comment success!");
                commentResponse.setComment(comment);
            } else {
                commentResponse.setStatus("400");
                commentResponse.setMessage("You do not have permission to delete this comment!");
                commentResponse.setComment(comment);
            }

        } else {
            commentResponse.setStatus("400");
            commentResponse.setMessage("Your account has expired!");
            commentResponse.setComment(null);
        }
        return commentResponse;
    }



}
