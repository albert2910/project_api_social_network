package com.example.demospringsecurity.mapperImpl;

import com.example.demospringsecurity.dto.UserViewDto;
import com.example.demospringsecurity.dto.request.ChangeInfoUserRequest;
import com.example.demospringsecurity.mapper.UserChangeInfoMapper;
import com.example.demospringsecurity.model.UserInfo;
import org.springframework.stereotype.Component;

@Component
public class UserChangeInfoMapperImpl implements UserChangeInfoMapper {
    @Override
    public UserInfo toEntity(ChangeInfoUserRequest changeInfoUserRequest) {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(changeInfoUserRequest.getUserId());
        userInfo.setUserName(changeInfoUserRequest.getUserName());
        userInfo.setUserFullName(changeInfoUserRequest.getUserFullName());
        userInfo.setUserAvatar(changeInfoUserRequest.getUserAvatar());
        userInfo.setUserEmail(changeInfoUserRequest.getUserEmail());
        userInfo.setUserBirthDate(changeInfoUserRequest.getUserBirthDate());
        userInfo.setUserAddress(changeInfoUserRequest.getUserAddress());
        return userInfo;
    }

    @Override
    public UserViewDto toDto(UserInfo entity) {
        UserViewDto userViewDto = new UserViewDto();
        userViewDto.setUsername(entity.getUserName());
        userViewDto.setUserFullName(entity.getUserFullName());
        userViewDto.setUserAvatar(entity.getUserAvatar());
        userViewDto.setUserEmail(entity.getUserEmail());
        userViewDto.setUserBirthDate(entity.getUserBirthDate());
        userViewDto.setUserAddress(entity.getUserAddress());
        return null;
    }
}
