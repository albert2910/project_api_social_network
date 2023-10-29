package com.example.demospringsecurity.mapper;

import com.example.demospringsecurity.dto.PostDto;
import com.example.demospringsecurity.dto.PostViewDto;
import com.example.demospringsecurity.dto.request.UpPostRequest;
import com.example.demospringsecurity.model.UserPost;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PostViewMapper {
    UserMapper MAPPER = Mappers.getMapper( UserMapper.class );

    UserPost toEntity(UpPostRequest dto);
    PostViewDto toDto(UserPost entity);
}
