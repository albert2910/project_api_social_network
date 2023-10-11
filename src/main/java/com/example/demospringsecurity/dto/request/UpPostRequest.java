package com.example.demospringsecurity.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpPostRequest {
    private int postId;
    private String postContent;
    private List<String> postUrlImages;
    private int postUserId;
    private LocalDateTime postCreateDate;
}
