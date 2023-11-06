package com.example.demospringsecurity.mapperImpl;

import com.example.demospringsecurity.dto.FriendDto;
import com.example.demospringsecurity.exceptions.UserNotFoundException;
import com.example.demospringsecurity.mapper.FriendMapper;
import com.example.demospringsecurity.model.Friend;
import com.example.demospringsecurity.model.UserInfo;
import com.example.demospringsecurity.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FriendMapperImpl implements FriendMapper {
    @Autowired
    UserInfoRepository userInfoRepository;
    @Override
    public Friend toEntity(FriendDto dto) {
        return null;
    }

    @Override
    public FriendDto toDto(Friend entity) {
        FriendDto friendDto = new FriendDto();
        UserInfo userReceiver = userInfoRepository.findByUserId(entity.getUserReceiverId()).orElseThrow(() -> new UserNotFoundException("Not found user!"));
        UserInfo userSender = userInfoRepository.findByUserId(entity.getUserSenderId()).orElseThrow(() -> new UserNotFoundException("Not found user!"));
        friendDto.setUsernameReceiver(userReceiver.getUserName());
        friendDto.setUsernameSender(userSender.getUserName());
        friendDto.setId(entity.getId());
        return friendDto;
    }
}
