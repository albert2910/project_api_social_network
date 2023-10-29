package com.example.demospringsecurity.response.post;

import com.example.demospringsecurity.dto.PostDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetNewFeedResponse {
    private String status;
    private String message;
    private List<PostDto> postDtos;
}
