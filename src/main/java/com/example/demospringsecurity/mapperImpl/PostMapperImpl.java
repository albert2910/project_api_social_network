package com.example.demospringsecurity.mapperImpl;

import com.example.demospringsecurity.dto.PostDto;
import com.example.demospringsecurity.dto.request.UpPostRequest;
import com.example.demospringsecurity.mapper.PostMapper;
import com.example.demospringsecurity.model.UserPost;
import org.springframework.stereotype.Component;

@Component
public class PostMapperImpl implements PostMapper {
    @Override
    public UserPost toEntity(UpPostRequest dto) {
        UserPost userPost = new UserPost();
        if (dto.getPostId() != 0) {
            userPost.setPostId(dto.getPostId());
        }
        userPost.setPostContent(dto.getPostContent());
        userPost.setPostUserId(dto.getPostUserId());
        userPost.setPostCreateDate(dto.getPostCreateDate());
        userPost.setPostCreateDate(dto.getPostCreateDate());
        return userPost;
    }

    @Override
    public PostDto toDto(UserPost entity) {
        PostDto postDto = new PostDto();
        postDto.setPostId(entity.getPostId());
        postDto.setPostContent(entity.getPostContent());
        postDto.setPostUserId(entity.getPostUserId());
        postDto.setPostCreateDate(entity.getPostCreateDate());
        postDto.setPostDeleteFlag(entity.getPostDeleteFlag());
        return postDto;
    }
}
