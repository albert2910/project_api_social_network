package com.example.demospringsecurity.response.comment;

import com.example.demospringsecurity.model.Comment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommentResponse {
    private String status;
    private String message;
    private Comment comment;
}
