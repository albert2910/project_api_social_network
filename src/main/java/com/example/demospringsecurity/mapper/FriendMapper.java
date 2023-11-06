package com.example.demospringsecurity.mapper;

import com.example.demospringsecurity.dto.FriendDto;
import com.example.demospringsecurity.dto.PostDto;
import com.example.demospringsecurity.dto.request.UpPostRequest;
import com.example.demospringsecurity.model.Friend;
import com.example.demospringsecurity.model.UserPost;
import org.mapstruct.Mapper;

@Mapper
public interface FriendMapper {
    Friend toEntity(FriendDto dto);
    FriendDto toDto(Friend entity);
}
