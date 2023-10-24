package com.example.demospringsecurity.service;

import com.example.demospringsecurity.dto.ReportUserDto;
import com.example.demospringsecurity.dto.request.AuthChangePassword;
import com.example.demospringsecurity.dto.request.ChangeInfoUserRequest;
import com.example.demospringsecurity.dto.request.RegisterRequest;
import com.example.demospringsecurity.exceptions.UserNotFoundException;
import com.example.demospringsecurity.mapperImpl.UserChangeInfoMapper;
import com.example.demospringsecurity.mapperImpl.UserMapper;
import com.example.demospringsecurity.model.ExcelGenerator;
import com.example.demospringsecurity.model.PasswordResetToken;
import com.example.demospringsecurity.model.UserInfo;
import com.example.demospringsecurity.repository.*;
import com.example.demospringsecurity.response.ChangeInfoUserResponse;
import com.example.demospringsecurity.response.PasswordChangeResponse;
import com.example.demospringsecurity.response.PasswordResetTokenResponse;
import com.example.demospringsecurity.response.RegisterResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class UserService {
    @Autowired
    UserInfoRepository userInfoRepository;

    @Autowired
    UserPostRepository userPostRepository;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    FriendRepository friendRepository;

    @Autowired
    LikeRepository likeRepository;

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
        Optional<UserInfo> userInfo = Optional.ofNullable(userInfoRepository.findByUserName(username).orElseThrow(() -> new UserNotFoundException("Not found user has userName: " + username)));
//        check tài khoản tồn tại bởi username
        Optional<PasswordResetToken> passwordResetToken = passwordResetTokenRepository.findPasswordResetTokenByUserId(userInfo.get().getUserId());
        String tokenReset = UUID.randomUUID().toString();
        PasswordResetToken passwordResetTokenAdd = new PasswordResetToken();
        passwordResetTokenAdd.setTokenReset(tokenReset);
        LocalDateTime dateResetToken = java.time.LocalDateTime.now();
        passwordResetTokenAdd.setPasswordResetToken_datetime(dateResetToken);
        passwordResetTokenAdd.setUserId(userInfo.get().getUserId());
        passwordResetToken.ifPresent(resetToken -> passwordResetTokenAdd.setPasswordResetTokenId(resetToken.getPasswordResetTokenId()));
        passwordResetTokenRepository.save(passwordResetTokenAdd);
        passwordResetTokenResponse.setStatus("200");
        passwordResetTokenResponse.setResetToken(tokenReset);
        passwordResetTokenResponse.setMessage("New token: " + passwordResetTokenResponse.getResetToken());

        return passwordResetTokenResponse;
    }

    public PasswordChangeResponse changePassword(AuthChangePassword authChangePassword) {
        PasswordChangeResponse passwordChangeResponse = new PasswordChangeResponse();
        if (authChangePassword.getNewPassword() == authChangePassword.getReNewPassword()) {
            passwordChangeResponse.setStatus("400");
            passwordChangeResponse.setMessage("Invalid request! New password not equal renew password!");
            return passwordChangeResponse;
        }
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findPasswordResetTokenByTokenReset(
                authChangePassword.getTokenReset());
        if (passwordResetToken != null) {
            if (!checkTimeTokenReset(passwordResetToken)) {
                passwordChangeResponse.setStatus("406");
                passwordChangeResponse.setMessage("token has expired");
            } else {
                Optional<UserInfo> userInfo = userInfoRepository.findByUserId(passwordResetToken.getUserId());
                if (userInfo.isPresent()) {
                    UserInfo userInfoChangePassword = new UserInfo();
                    userInfoChangePassword.setUserId(userInfo.get().getUserId());
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
            Optional<UserInfo> userInfo = Optional.ofNullable(userInfoRepository.findByUserName(currentUserName).orElseThrow(() -> new UserNotFoundException("Not found User username: " + currentUserName)));
            changeInfoUserRequest.setUserId(userInfo.get().getUserId());
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
            if (changeInfoUserRequest.getUserEmail() == null || changeInfoUserRequest.getUserEmail().isEmpty()) {
                userInfoUpdate.setUserEmail(userInfo.get().getUserEmail());
            }
            if (changeInfoUserRequest.getUserBirthDate() == null) {
                userInfoUpdate.setUserBirthDate(userInfo.get().getUserBirthDate());
            }
            if (changeInfoUserRequest.getUserAddress() == null || changeInfoUserRequest.getUserAddress().isEmpty()) {
                userInfoUpdate.setUserAddress(userInfo.get().getUserAddress());
            }
            userInfoUpdate.setUserPassword(userInfo.get().getUserPassword());
            userInfoUpdate.setRoles("ROLE_USER");
            userInfoRepository.save(userInfoUpdate);
            changeInfoUserResponse.setStatus("200");
            changeInfoUserResponse.setMessage("Update success");
            changeInfoUserResponse.setUserInfoUpdate(userInfoUpdate);
        } else {
            changeInfoUserResponse.setStatus("400");
            changeInfoUserResponse.setMessage("Token is invalid!");
        }
        return changeInfoUserResponse;
    }

    public UserInfo findUserById(int id) {
        return userInfoRepository.findByUserId(id).orElseThrow(() -> new UserNotFoundException("Not found user!"));
    }

    //  export file exel bao cao 1 tuan cua user dang nhap
    public void exportReport(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=userActive" + currentDateTime + ".xlsx";
        response.setHeader(headerKey,
                headerValue);

        ReportUserDto reportUserDto = synthesizeReport();
        ExcelGenerator generator = new ExcelGenerator(reportUserDto);
        generator.generateExcelFile(response);
    }

    //   tổng hợp báo cáo 1 tuần của user đăng nhập
    public ReportUserDto synthesizeReport() {
        ReportUserDto reportUserDto = new ReportUserDto();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            UserInfo userInfo = userInfoRepository.findByUserName(currentUserName).get();
            reportUserDto.setUserName(currentUserName);
//            lay ra so bai viet user dang nhap da tao trong vong 1 tuan
            LocalDateTime currentDate = LocalDateTime.now();
            int countPostsLastWeek = userPostRepository.countPostLastWeek(userInfo.getUserId(),
                    currentDate);
            reportUserDto.setPostsLastWeek(countPostsLastWeek);
//            lay ra so comment user dang nhap da comment trong vong 1 tuan
            int countCommentsLastWeek = commentRepository.countCommentsLastWeekByMe(userInfo.getUserId(),
                    currentDate);
            reportUserDto.setNewCommentsLastWeek(countCommentsLastWeek);
//            lay ra danh sach ban be user da ket ban trong vong 1 tuan
            int countNewFriendsLastWeek = friendRepository.countNewFriendsLastWeek(userInfo.getUserId(),
                    currentDate);
            reportUserDto.setNewFriendLastWeek(countNewFriendsLastWeek);
//            lay ra so like user dang nhap da like trong vong 1 tuan
            int countLikesLastWeek = likeRepository.countLikesLastWeekByMe(userInfo.getUserId(),
                    currentDate);
            reportUserDto.setNewLikesLastWeek(countLikesLastWeek);
        }
        return reportUserDto;
    }

}