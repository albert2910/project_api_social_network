package com.example.demospringsecurity.mapperImpl;

import com.example.demospringsecurity.dto.PostDto;
import com.example.demospringsecurity.dto.request.UpPostRequest;
import com.example.demospringsecurity.model.UserPost;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PostMapper {
    UserMapper MAPPER = Mappers.getMapper( UserMapper.class );

    UserPost toEntity(UpPostRequest dto);
    PostDto toDto(UserPost entity);
}
