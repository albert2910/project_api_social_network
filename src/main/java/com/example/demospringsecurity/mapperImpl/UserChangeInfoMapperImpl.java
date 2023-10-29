package com.example.demospringsecurity.mapperImpl;

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
    public ChangeInfoUserRequest toDto(UserInfo entity) {
        return null;
    }
}
