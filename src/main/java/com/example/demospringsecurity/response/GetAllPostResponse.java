package com.example.demospringsecurity.response;

import com.example.demospringsecurity.dto.PostDto;
import com.example.demospringsecurity.model.UserPost;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAllPostResponse {
    private String status;
    private String message;
    private List<PostDto> posts;
}
