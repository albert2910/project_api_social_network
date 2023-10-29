package com.example.demospringsecurity.mapper;

import com.example.demospringsecurity.dto.request.ChangeInfoUserRequest;
import com.example.demospringsecurity.model.UserInfo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
@Mapper
public interface UserChangeInfoMapper {
//    UserChangeInfoMapper MAPPER = Mappers.getMapper( UserChangeInfoMapper.class );

    UserInfo toEntity(ChangeInfoUserRequest dto);
    ChangeInfoUserRequest toDto(UserInfo entity);
}
