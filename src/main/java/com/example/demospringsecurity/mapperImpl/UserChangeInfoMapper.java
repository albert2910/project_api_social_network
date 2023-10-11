package com.example.demospringsecurity.mapperImpl;

import com.example.demospringsecurity.dto.request.ChangeInfoUserRequest;
import com.example.demospringsecurity.model.UserInfo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
@Mapper
public interface UserChangeInfoMapper {
    UserMapper MAPPER = Mappers.getMapper( UserMapper.class );

    UserInfo toEntity(ChangeInfoUserRequest dto);
    ChangeInfoUserRequest toDto(UserInfo entity);
}
