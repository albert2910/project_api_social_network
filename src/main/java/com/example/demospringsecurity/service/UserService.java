package com.example.demospringsecurity.service;

import com.example.demospringsecurity.dto.request.AuthChangePassword;
import com.example.demospringsecurity.dto.request.ChangeInfoUserRequest;
import com.example.demospringsecurity.dto.request.RegisterRequest;
import com.example.demospringsecurity.mapperImpl.UserChangeInfoMapper;
import com.example.demospringsecurity.mapperImpl.UserMapper;
import com.example.demospringsecurity.model.PasswordResetToken;
import com.example.demospringsecurity.model.UserInfo;
import com.example.demospringsecurity.repository.PasswordResetTokenRepository;
import com.example.demospringsecurity.repository.UserInfoRepository;
import com.example.demospringsecurity.response.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    UserInfoRepository userInfoRepository;

    @Autowired
    UserMapper userMapper;

    @Autowired
    UserChangeInfoMapper userChangeInfoMapper;

    @Autowired
    PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    FileService fileService;

    /**
     * đăng ký tài khoản
     *
     * @param registerRequest
     * @return
     */
    public RegisterResponse registerUser(RegisterRequest registerRequest) {
        RegisterResponse registerResponse = new RegisterResponse();
        if (registerRequest != null) {
//            check trùng email
            if (userInfoRepository.existsUserInfoByUserEmail(registerRequest.getUserEmail())) {
                registerResponse.setMessage("Email already exists!");
                registerResponse.setSuccess(false);
                registerResponse.setUserInfo(null);
            } else if (userInfoRepository.existsUserInfosByUserName(registerRequest.getUserName())) {
                registerResponse.setMessage("Username already exists!");
                registerResponse.setSuccess(false);
                registerResponse.setUserInfo(null);
            } else {
                UserInfo userInfo = userMapper.toEntity(registerRequest);
                userInfo.setUserPassword(new BCryptPasswordEncoder().encode(registerRequest.getUserPassword()));
                userInfo.setRoles("ROLE_USER");
                userInfoRepository.save(userInfo);
                registerResponse.setMessage("Register success!");
                registerResponse.setSuccess(true);
                registerResponse.setUserInfo(userInfo);
            }
        } else {
            throw new NullPointerException();
        }
        return registerResponse;
    }

    /**
     * quên mật khẩu trả về token
     *
     * @param username
     * @return
     */
    public PasswordResetTokenResponse forgotPassword(String username) {
        PasswordResetTokenResponse passwordResetTokenResponse = new PasswordResetTokenResponse();
        Optional<UserInfo> userInfo = userInfoRepository.findByUserName(username);
//        check tài khoản tồn tại bởi username
        if (!userInfo.isPresent()) {
            passwordResetTokenResponse.setStatus("404");
            passwordResetTokenResponse.setMessage("Not found user!");
        } else {
            Optional<PasswordResetToken> passwordResetToken = passwordResetTokenRepository.findPasswordResetTokenByUserId(userInfo.get().getUserId());
            String tokenReset = UUID.randomUUID().toString();
            PasswordResetToken passwordResetTokenAdd = new PasswordResetToken();
            passwordResetTokenAdd.setTokenReset(tokenReset);
            LocalDateTime dateResetToken = java.time.LocalDateTime.now();
            passwordResetTokenAdd.setPasswordResetToken_datetime(dateResetToken);
            passwordResetTokenAdd.setUserId(userInfo.get().getUserId());
            if (passwordResetToken.isPresent()) {
                passwordResetTokenAdd.setPasswordResetTokenId(passwordResetToken.get().getPasswordResetTokenId());
            }
            passwordResetTokenRepository.save(passwordResetTokenAdd);
            passwordResetTokenResponse.setStatus("200");
            passwordResetTokenResponse.setResetToken(tokenReset);
            passwordResetTokenResponse.setMessage("New token: " + passwordResetTokenResponse.getResetToken());
        }
        return passwordResetTokenResponse;
    }

    public PasswordChangeResponse changePassword(AuthChangePassword authChangePassword) {
        PasswordChangeResponse passwordChangeResponse = new PasswordChangeResponse();
        if (authChangePassword.getNewPassword() == authChangePassword.getReNewPassword()) {
            passwordChangeResponse.setStatus("400");
            passwordChangeResponse.setMessage("Invalid request! New password not equal renew password!");
            return passwordChangeResponse;
        }
        Optional<PasswordResetToken> passwordResetToken = passwordResetTokenRepository.findPasswordResetTokenByUserIdAndAndTokenReset(authChangePassword.getUserId(),
                authChangePassword.getTokenReset());
        if (passwordResetToken.isPresent()) {
            if (!checkTimeTokenReset(passwordResetToken.get())) {
                passwordChangeResponse.setStatus("406");
                passwordChangeResponse.setMessage("token has expired");
            } else {
                Optional<UserInfo> userInfo = userInfoRepository.findByUserId(authChangePassword.getUserId());
                if (userInfo.isPresent()) {
                    UserInfo userInfoChangePassword = new UserInfo();
                    userInfoChangePassword.setUserId(authChangePassword.getUserId());
                    userInfoChangePassword.setUserEmail(userInfo.get().getUserEmail());
                    userInfoChangePassword.setUserPassword(new BCryptPasswordEncoder().encode(authChangePassword.getNewPassword()));
                    userInfoChangePassword.setUserName(userInfo.get().getUserName());
                    userInfoChangePassword.setRoles("ROLE_USER");
                    userInfoRepository.save(userInfoChangePassword);
                    passwordChangeResponse.setStatus("200");
                    passwordChangeResponse.setMessage("Reset password success!");
                } else {
                    passwordChangeResponse.setStatus("404");
                    passwordChangeResponse.setMessage("Not found user!");
                }
            }
        } else {
            passwordChangeResponse.setStatus("404");
            passwordChangeResponse.setMessage("Token is invalid!");
        }

        return passwordChangeResponse;
    }


    public boolean checkTimeTokenReset(PasswordResetToken passwordResetToken) {
        LocalDateTime currentDateTime = java.time.LocalDateTime.now();
        long millisToken = passwordResetToken.getPasswordResetToken_datetime()
                .atZone(ZoneId.systemDefault())
                .toInstant().toEpochMilli();
        long millisCurrent = currentDateTime
                .atZone(ZoneId.systemDefault())
                .toInstant().toEpochMilli();
        long millis = millisCurrent - millisToken;
        if (millis < 120000) {
            return true;
        } else {
            passwordResetTokenRepository.deleteById(passwordResetToken.getPasswordResetTokenId());
            return false;
        }
    }

    public ChangeInfoUserResponse updateInfoUser(ChangeInfoUserRequest changeInfoUserRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ChangeInfoUserResponse changeInfoUserResponse = new ChangeInfoUserResponse();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            UserInfo userInfo = userInfoRepository.findByUserName(currentUserName).get();
            changeInfoUserRequest.setUserId(userInfo.getUserId());
        }
        Optional<UserInfo> userInfo = userInfoRepository.findByUserId(changeInfoUserRequest.getUserId());

        if (userInfo.isPresent()) {
            UserInfo userInfoUpdate = userChangeInfoMapper.toEntity(changeInfoUserRequest);
            if (changeInfoUserRequest.getUserName() == null || changeInfoUserRequest.getUserName().isEmpty()) {
                userInfoUpdate.setUserName(userInfo.get().getUserName());
            }
            if (changeInfoUserRequest.getUserFullName() == null || changeInfoUserRequest.getUserFullName().isEmpty()) {
                userInfoUpdate.setUserFullName(userInfo.get().getUserFullName());
            }
            if (changeInfoUserRequest.getUserAvatar() == null || changeInfoUserRequest.getUserAvatar().isEmpty()) {
                userInfoUpdate.setUserAvatar(userInfo.get().getUserAvatar());
            }
            if(changeInfoUserRequest.getUserEmail() == null || changeInfoUserRequest.getUserEmail().isEmpty()) {
                userInfoUpdate.setUserEmail(userInfo.get().getUserEmail());
            }
            if(changeInfoUserRequest.getUserBirthDate() == null) {
                userInfoUpdate.setUserBirthDate(userInfo.get().getUserBirthDate());
            }
            if(changeInfoUserRequest.getUserAddress() == null || changeInfoUserRequest.getUserAddress().isEmpty()) {
                userInfoUpdate.setUserAddress(userInfo.get().getUserAddress());
            }
            userInfoUpdate.setUserPassword(userInfo.get().getUserPassword());
            userInfoUpdate.setRoles("ROLE_USER");
            userInfoRepository.save(userInfoUpdate);
            changeInfoUserResponse.setStatus("200");
            changeInfoUserResponse.setMessage("Update success");
            changeInfoUserResponse.setUserInfoUpdate(userInfoUpdate);
        } else {
            changeInfoUserResponse.setStatus("404");
            changeInfoUserResponse.setMessage("Not found user!");
        }
        return changeInfoUserResponse;
    }
    public UserInfo findUserById(int id) {
        return userInfoRepository.findByUserId(id).get();
    }
}
