package com.example.demospringsecurity.service;

import com.example.demospringsecurity.dto.request.AuthChangePassword;
import com.example.demospringsecurity.dto.request.RegisterRequest;
import com.example.demospringsecurity.exceptions.TokenNotFoundException;
import com.example.demospringsecurity.exceptions.UserNotFoundException;
import com.example.demospringsecurity.mapperImpl.UserChangeInfoMapper;
import com.example.demospringsecurity.mapperImpl.UserMapper;
import com.example.demospringsecurity.model.PasswordResetToken;
import com.example.demospringsecurity.model.UserInfo;
import com.example.demospringsecurity.repository.*;
import com.example.demospringsecurity.response.PasswordChangeResponse;
import com.example.demospringsecurity.response.PasswordResetTokenResponse;
import com.example.demospringsecurity.response.RegisterResponse;
import org.apache.poi.sl.draw.geom.GuideIf;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @InjectMocks
    UserService userService;
    @Mock
    UserInfoRepository userInfoRepository;

    @Mock
    AuthChangePassword authChangePassword;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    UserPostRepository userPostRepository;

    @Mock
    CommentRepository commentRepository;

    @Mock
    FriendRepository friendRepository;

    @Mock
    LikeRepository likeRepository;

    @Mock
    UserMapper userMapper;

    @Mock
    UserChangeInfoMapper userChangeInfoMapper;

    @Mock
    PasswordResetTokenRepository passwordResetTokenRepository;

    @Mock
    FileService fileService;

    RegisterRequest registerRequest = new RegisterRequest("abc@gmail.com",
            "test",
            "Abc1234@");

    @Test
    void register_fail1() {

        Mockito.when(userInfoRepository.existsUserInfoByUserEmail(Mockito.anyString()))
                .thenReturn(true);
        RegisterResponse registerResponse = userService.registerUser(registerRequest);
        Assertions.assertEquals(false,
                registerResponse.isSuccess());
        Assertions.assertEquals(null,
                registerResponse.getUserInfo());
        Assertions.assertEquals("Email already exists!",
                registerResponse.getMessage());
    }

    @Test
    void register_fail2() {
        Mockito.when(userInfoRepository.existsUserInfosByUserName(Mockito.anyString()))
                .thenReturn(true);
        RegisterResponse registerResponse = userService.registerUser(registerRequest);
        Assertions.assertEquals(false,
                registerResponse.isSuccess());
        Assertions.assertEquals(null,
                registerResponse.getUserInfo());
        Assertions.assertEquals("Username already exists!",
                registerResponse.getMessage());
    }

    @Test
    void register_success() {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserName(registerRequest.getUserName());
        userInfo.setUserPassword(registerRequest.getUserPassword());
        userInfo.setUserEmail(registerRequest.getUserEmail());
        Mockito.when(userInfoRepository.existsUserInfoByUserEmail(Mockito.anyString()))
                .thenReturn(false);
        Mockito.when(userInfoRepository.existsUserInfosByUserName(Mockito.anyString()))
                .thenReturn(false);
        Mockito.when(userMapper.toEntity(registerRequest))
                .thenReturn(userInfo);
        RegisterResponse registerResponse = userService.registerUser(registerRequest);
        Assertions.assertEquals(true,
                registerResponse.isSuccess());
        Assertions.assertEquals("Register success!",
                registerResponse.getMessage());
    }

    @Test
    void forgotPassword_userNotFoundException() {
        Mockito.when(userInfoRepository.findByUserName(Mockito.anyString()))
                .thenThrow(UserNotFoundException.class);
        Assertions.assertThrows(UserNotFoundException.class,
                () -> {
                    userService.forgotPassword(Mockito.anyString());
                });
    }

    @Test
    void forgotPassword_success() {
        Optional<UserInfo> userInfo = Optional.of(new UserInfo());
        Mockito.when(userInfoRepository.findByUserName(Mockito.anyString())).thenReturn(userInfo);
        Optional<PasswordResetToken> passwordResetToken = Optional.of(new PasswordResetToken());
        Mockito.when(passwordResetTokenRepository.findPasswordResetTokenByUserId(Mockito.anyInt())).thenReturn(passwordResetToken);
        PasswordResetTokenResponse passwordResetTokenResponseActual = userService.forgotPassword(Mockito.anyString());
//        PasswordResetTokenResponse passwordResetTokenResponseExpected = new PasswordResetTokenResponse("200","sadsadasdasdas", "sASasASasAS");
        Assertions.assertEquals("200",passwordResetTokenResponseActual.getStatus());
    }

    @Test
    void changePassword_tokenNotFoundException() {
        authChangePassword.setNewPassword("asdsadasdas");
        authChangePassword.setTokenReset("podfgidfjgio");
        Mockito.when(authChangePassword.getTokenReset()).thenReturn("asdasdasdas");
        Mockito.when(passwordResetTokenRepository.findPasswordResetTokenByTokenReset(Mockito.anyString()))
                .thenThrow(TokenNotFoundException.class);
        Assertions.assertThrows(TokenNotFoundException.class,
                () -> {
                    userService.changePassword(authChangePassword);
                });
    }

    @Test
    void changePassword_timeOutResetToken() {
        authChangePassword.setNewPassword("asdsadasdas");
        authChangePassword.setTokenReset("podfgidfjgio");
        Optional<PasswordResetToken> passwordResetToken = Optional.of(new PasswordResetToken());
        LocalDateTime localDateTime = LocalDateTime.of(2000,10,29,12,15,25,35);
        passwordResetToken.get().setPasswordResetToken_datetime(localDateTime);

        Mockito.when(authChangePassword.getTokenReset()).thenReturn("asdasdasdas");
        Mockito.when(passwordResetTokenRepository.findPasswordResetTokenByTokenReset(Mockito.anyString())).thenReturn(passwordResetToken);
        PasswordChangeResponse passwordChangeResponse = userService.changePassword(authChangePassword);
        Assertions.assertEquals("406",passwordChangeResponse.getStatus());
        Assertions.assertEquals("Token has expired",passwordChangeResponse.getMessage());

    }

    @Test
    void changePassword_userNotFoundException() {
        authChangePassword.setNewPassword("asdsadasdas");
        authChangePassword.setTokenReset("podfgidfjgio");
        Optional<PasswordResetToken> passwordResetToken = Optional.of(new PasswordResetToken());
        LocalDateTime localDateTime = LocalDateTime.now();
        passwordResetToken.get().setPasswordResetToken_datetime(localDateTime);
        Mockito.when(authChangePassword.getTokenReset()).thenReturn("asdasdasdas");
        Mockito.when(passwordResetTokenRepository.findPasswordResetTokenByTokenReset(Mockito.anyString())).thenReturn(passwordResetToken);
        Mockito.when(userInfoRepository.findByUserId(Mockito.anyInt())).thenThrow(UserNotFoundException.class);
        Assertions.assertThrows(UserNotFoundException.class,
                () -> {
                    userService.changePassword(authChangePassword);
                });

    }
    @Test
    void changePassword_success() {
        authChangePassword.setNewPassword("asdsadasdas");
        authChangePassword.setTokenReset("podfgidfjgio");
        Optional<PasswordResetToken> passwordResetToken = Optional.of(new PasswordResetToken());
        Optional<UserInfo> userInfo = Optional.of(new UserInfo());
        LocalDateTime localDateTime = LocalDateTime.now();
        passwordResetToken.get().setPasswordResetToken_datetime(localDateTime);
        Mockito.when(authChangePassword.getTokenReset()).thenReturn("asdasdasdas");
        Mockito.when(passwordResetTokenRepository.findPasswordResetTokenByTokenReset(Mockito.anyString())).thenReturn(passwordResetToken);
        Mockito.when(userInfoRepository.findByUserId(Mockito.anyInt())).thenReturn(userInfo);
        PasswordChangeResponse passwordChangeResponse = userService.changePassword(authChangePassword);
        Assertions.assertEquals("200",passwordChangeResponse.getStatus());
        Assertions.assertEquals("Reset password success!",passwordChangeResponse.getMessage());

    }


}
