package com.example.demospringsecurity.mapperImpl;

import com.example.demospringsecurity.dto.request.RegisterRequest;
import com.example.demospringsecurity.mapper.UserMapper;
import com.example.demospringsecurity.model.UserInfo;
import org.springframework.stereotype.Component;

@Component
public class UserRegisterMapperImpl implements UserMapper {
    @Override
    public UserInfo toEntity(RegisterRequest dto) {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserEmail(dto.getUserEmail());
        userInfo.setUserName(dto.getUserName());
        userInfo.setUserPassword(dto.getUserPassword());
        return userInfo;
    }

    @Override
    public RegisterRequest toDto(UserInfo entity) {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUserEmail(entity.getUserEmail());
        registerRequest.setUserName(entity.getUserName());
        registerRequest.setUserPassword(entity.getUserPassword());
        return registerRequest;
    }
}
