package com.example.demospringsecurity.mapper;

import com.example.demospringsecurity.dto.PostDto;
import com.example.demospringsecurity.dto.request.UpPostRequest;
import com.example.demospringsecurity.model.UserPost;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PostMapper {
//    PostMapper MAPPER = Mappers.getMapper( PostMapper.class );

    UserPost toEntity(UpPostRequest dto);
    PostDto toDto(UserPost entity);
}
