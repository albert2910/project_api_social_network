package com.example.demospringsecurity.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(type = "object", example = "{\n" +
        "  \"commentContent\": \"string\"" +
        "}")
public class CommentRequest {
    private int commentId;
    private String commentContent;
    private int commentPostId;
    private int commentUserId;
    private LocalDateTime commentCreateDate;
}
