package com.example.demospringsecurity.response.post;

import com.example.demospringsecurity.dto.PostDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetMyPostsResponse {
    private int status;
    private String message;
    private int userId;
    private List<PostDto> myPosts;
}
