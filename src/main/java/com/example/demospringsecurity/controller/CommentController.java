package com.example.demospringsecurity.controller;

import com.example.demospringsecurity.dto.request.CommentRequest;
import com.example.demospringsecurity.response.comment.CommentResponse;
import com.example.demospringsecurity.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/comments")
public class CommentController {
    @Autowired
    CommentService commentService;

    @PostMapping("/{postId}")
    public CommentResponse postComment(@PathVariable int postId, @RequestBody CommentRequest commentRequest) {
        commentRequest.setCommentPostId(postId);
        return commentService.postComment(commentRequest);
    }

    @PutMapping("/{commentId}")
    public CommentResponse editComment(@PathVariable int commentId, @RequestBody CommentRequest commentRequest) {
        commentRequest.setCommentId(commentId);
        return commentService.postComment(commentRequest);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable int commentId) {
        return new ResponseEntity<>(commentService.deleteComment(commentId),
                HttpStatus.OK);
    }
}
