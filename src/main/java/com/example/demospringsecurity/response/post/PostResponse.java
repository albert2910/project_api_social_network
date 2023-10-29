package com.example.demospringsecurity.response.post;

import com.example.demospringsecurity.dto.PostDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostResponse {
    private String message;
    private String status;
    private PostDto postDto;
}
