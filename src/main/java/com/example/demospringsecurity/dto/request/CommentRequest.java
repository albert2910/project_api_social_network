package com.example.demospringsecurity.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentRequest {
    private int commentId;
    private String commentContent;
    private int commentPostId;
    private int commentUserId;
    private LocalDateTime commentCreateDate;
}
