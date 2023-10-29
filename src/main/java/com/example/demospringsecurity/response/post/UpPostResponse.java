package com.example.demospringsecurity.response.post;

import com.example.demospringsecurity.dto.PostDto;
import com.example.demospringsecurity.dto.PostViewDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpPostResponse {
    private String status;
    private String message;
    private PostViewDto postViewDto;
}
