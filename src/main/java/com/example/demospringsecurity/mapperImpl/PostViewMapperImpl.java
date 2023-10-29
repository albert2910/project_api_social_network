package com.example.demospringsecurity.mapperImpl;

import com.example.demospringsecurity.dto.PostDto;
import com.example.demospringsecurity.dto.PostViewDto;
import com.example.demospringsecurity.dto.request.UpPostRequest;
import com.example.demospringsecurity.mapper.PostMapper;
import com.example.demospringsecurity.mapper.PostViewMapper;
import com.example.demospringsecurity.model.UserPost;
import org.springframework.stereotype.Component;

@Component
public class PostViewMapperImpl implements PostViewMapper {
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
    public PostViewDto toDto(UserPost entity) {
        PostViewDto postViewDto = new PostViewDto();
        postViewDto.setPostId(entity.getPostId());
        postViewDto.setPostContent(entity.getPostContent());
        postViewDto.setPostUserId(entity.getPostUserId());
        postViewDto.setPostCreateDate(entity.getPostCreateDate());
        postViewDto.setPostDeleteFlag(entity.getPostDeleteFlag());
        return postViewDto;
    }
}
