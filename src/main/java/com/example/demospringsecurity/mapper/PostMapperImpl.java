package com.example.demospringsecurity.mapper;

import com.example.demospringsecurity.dto.PostDto;
import com.example.demospringsecurity.dto.request.UpPostRequest;
import com.example.demospringsecurity.mapperImpl.PostMapper;
import com.example.demospringsecurity.model.Image;
import com.example.demospringsecurity.model.UserPost;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PostMapperImpl implements PostMapper {
    @Override
    public UserPost toEntity(UpPostRequest dto) {
        UserPost userPost = new UserPost();
        userPost.setPostContent(dto.getPostContent());
        userPost.setPostUserId(dto.getPostUserId());
        userPost.setPostCreateDate(dto.getPostCreateDate());
        return userPost;
    }

    @Override
    public PostDto toDto(UserPost entity) {
        PostDto postDto = new PostDto();
        postDto.setPostId(entity.getPostId());
        postDto.setPostContent(entity.getPostContent());
        postDto.setPostUserId(entity.getPostUserId());
        return postDto;
    }
}
